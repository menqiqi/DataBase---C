#pragma once

#include<stdio.h>
#include<assert.h>
#include<stdlib.h>

typedef int TDataType;
typedef struct BTreeNode{
	TDataType data;
	struct BTreeNode *left;
	struct BTreeNode *right;
}BTreeNode;

//创建树的节点
BTreeNode *CreateNode(TDataType data)
{
	BTreeNode *node = (BTreeNode*)malloc(sizeof(BTreeNode));
	node->data = data;
	node->left = NULL;
	node->right = NULL;
	return node;
}

//创建树(难点)
//返回值：
//1.创出的树的根节点
//2.创建树的过程中，使用的字符个数(pUsedSize)
BTreeNode *CreateTree(int preOrder[], int size, int *pUsedSize)
{
	int leftUse;
	int rightUse;
	int rootValue = preOrder[0];
	BTreeNode *root;
	if(size <= 0)
	{
		*pUsedSize = 0;
		return NULL;
	}
	if(rootValue == -1){
		*pUsedSize = 1;
		return NULL;
	}
	root = CreateNode(rootValue);
	root->left = CreateTree(preOrder+1, size-1, &leftUse);
	root->right = CreateTree(preOrder+1+leftUse, size-1-leftUse, &rightUse);

	//汇总
	*pUsedSize = 1+leftUse+rightUse;

	return root;
}


//遍历树
//前序遍历树
void PreOrder(BTreeNode *root)
{
	if(root == NULL)//空树
	{
		return ;
	}
	printf("%d ",root->data);
	PreOrder(root->left);
	PreOrder(root->right);
}

//中序遍历树
void InOrder(BTreeNode *root)
{
	if(root == NULL)
	{
		return;
	}
	InOrder(root->left);
	printf("%d ",root->data);
	InOrder(root->right);
}

//后序遍历树
void PostOrder(BTreeNode *root)
{
	if(root == NULL)
	{
		return;
	}
	PostOrder(root->left);
	PostOrder(root->right);
	printf("%d ",root->data);
}

//求树的节点个数
//1.用子问题求解
//2.用遍历求解（但是一定要用到全局变量，这种方法不好）
int GetNodeSize(BTreeNode *root)
{
	int left;
	int right;
	if(root == NULL)
	{
		return 0;
	}
	left = GetNodeSize(root->left);
	right = GetNodeSize(root->right);
	return left+right+1;
}

//求叶子节点个数
int GetLeafSize(BTreeNode *root)
{
	int left;
	int right;
	if(root == NULL)//空树
	{
		return 0;
	}
	if((root->left==NULL) && (root->right==NULL))//只有一个节点的树
	{
		return 1;
	}
	left = GetLeafSize(root->left);
	right = GetLeafSize(root->right);
	return left+right;
}

//求第k层的节点数
int GetLevelKSize(BTreeNode *root, int k)
{
	int left;
	int right;
	assert(k>=1);
	if(root == NULL)//空树：任意层都是0个
	{
		return 0;
	}
	if(k == 1)//第一层只有一个节点
	{
		return 1;
	}
	//我的第K层，是我子树的第K-1层
	left = GetLevelKSize(root->left, k-1);
	right = GetLevelKSize(root->right, k-1);
	return left+right;
}

//求数的高度/深度
#define MAX(a,b) ((a)>(b)?(a):(b))
int GetHeight(BTreeNode *root)
{
	int left;
	int right;
	if(root == NULL)
	{
		return 0;
	}
	//当子树只有一个节点的时候，可以写，也可以不写
	//写的话节省两次函数调用
	left = GetHeight(root->left);
	right = GetHeight(root->right);
	return MAX(left,right)+1;
}
//
//找一个数是否在树中(难点)
//前提：树里的data不重复
//如果找到了，返回节点地址
//如果没找到，返回NULL
//先匹配根，再左子树，再右子树
BTreeNode *Find(BTreeNode *root, TDataType data)
{
	BTreeNode *result;
	if(root == NULL)
	{
		return NULL;
	}
	if(data == root->data)
	{
		return root;
	}
	result = Find(root->left, data);
	if(result != NULL)
	{
		//左子树找到了
		return result;
	}
	result = Find(root->right, data);
	if(result != NULL)
	{
		//右子树找到了
		return result;
	}
	else{
		return NULL;
	}
}





//二叉树的层序遍历(用队列实现)
#include "Queue.h"
void LevelOrder(BTreeNode *root)
{
	BTreeNode *front;
	Queue queue;
	QueueInit(&queue);

	if(root == NULL)
	{
		return;
	}
	QueuePush(&queue,root);//放的是节点的地址，不是data
	while(!IsEmpty(&queue)){
		front = QueueTop(&queue);
		QueuePop(&queue);

		//front开始拉下线，空节点是不要的
		//先左后右
		if(front->left != NULL)
		{
			QueuePush(&queue,front->left);
		}
		if(front->right != NULL)
		{
			QueuePush(&queue,front->right);
		}
		//层序遍历front节点
		printf("%d ",front->data);
	}
	//销毁队列（栈用了之后不必销毁，但队列必须销毁）
	QueueDestroy(&queue);
}




//判断一棵树是不是完全二叉树
//完全二叉树（层序遍历下来，空节点只能放在最后）
int IsCompleteTree(BTreeNode *root)
{
	BTreeNode *front;
	Queue queue;
	QueueInit(&queue);

	if(root == NULL)//空树是完全二叉树
	{
		return 1;
	}
	QueuePush(&queue,root);//放的是节点的地址，不是data

	while(!IsEmpty(&queue)){
		front = QueueTop(&queue);
		QueuePop(&queue);
		//这里和层序遍历的区别：front可能为空
		if(front == NULL)
		{
			//如果是完全二叉树，front后面应该都是空
			break;
		}
			QueuePush(&queue,front->left);
			QueuePush(&queue,front->right);
		
	}
	//队列剩余节点是否都是NULL
	//判定队列为空 QueueTop(&queue) == NULL,这个不是空队列判定
	while(!IsEmpty(&queue)){
		front = QueueTop(&queue);
		QueuePop(&queue);//如果不POP掉，front的值就永远都是同一个
		if(front != NULL)
		{
			//队列销毁
			QueueDestroy(&queue);
			return 0;
		}
	}

	//销毁队列（栈用了之后不必销毁，但队列必须销毁）
	QueueDestroy(&queue);
	return 1;
}


//按非递归的方式遍历（广度优先遍历用栈实现）
//前序
#include "Stack.h"
void PreOrderLoop(BTreeNode *root)
{
	BTreeNode *cur;
	BTreeNode *top;
	Stack stack;
	StackInit(&stack);

	cur = root;

	while(cur!=NULL || !StackEmpty(&stack)){
		while(cur != NULL){
			printf("%d ",cur->data);
			StackPush(&stack,cur);
			cur = cur->left;
		}
		//top的左子树和根已经遍历过了
		top = StackTop(&stack);
		StackPop(&stack);

		cur = top->right;
	}
}

//中序
void InOrderLoop(BTreeNode *root)
{

	BTreeNode *cur;
	BTreeNode *top;
	Stack stack;
	StackInit(&stack);

	cur = root;
	while(cur!=NULL || !StackEmpty(&stack)){
		while(cur != NULL){
			StackPush(&stack,cur);
			cur = cur->left;
		}
		top = StackTop(&stack);
		StackPop(&stack);
		printf("%d ",top->data);
		cur = top->right;
	}
}


//后序
void PostOrderLoop(BTreeNode *root)
{
	BTreeNode *cur;
	BTreeNode *top;
	BTreeNode *last = NULL;//记录上一个遍历完的节点
	Stack stack;
	StackInit(&stack);

	cur = root;
	while(!StackEmpty(&stack) || cur!=NULL){
		//遍历左子树
		while(cur != NULL){
			StackPush(&stack,cur);
			cur = cur->left;
		}
		//top的左子树已经遍历过了
		top = StackTop(&stack);
		if(top->right==NULL || top->right==last)
		{
			//如果右子树被遍历过了
			printf("%d ",top->data);
			StackPop(&stack);
		//记录这个被遍历的节点
			last = top;
	}
		else{
			//右子树没有被遍历过
			cur = top->right;
		}
	}
}



//求镜像(左子树和右子树都处理好，然后交换)
//递归
void Mirror1(BTreeNode *root)
{
	BTreeNode *tmp;
	if(root == NULL)
	{
		return;
	}
	Mirror1(root->left);
	Mirror1(root->right);
	tmp = root->left;
	root->left = root->right;
	root->right = tmp;
}

//非递归
void Mirror2(BTreeNode *root)
{
	BTreeNode *cur;
	BTreeNode *top;
	BTreeNode *last = NULL;
	BTreeNode *tmp;
	Stack stack;
	StackInit(&stack);

	cur = root;
	while(cur!=NULL || !StackEmpty(&stack)){
		//遍历左子树
		while(cur != NULL){
		StackPush(&stack,cur);
		cur = cur->left;
	}
	//top的左子树已经遍历过了
	top = StackTop(&stack);
	if(top->right==NULL || top->right==last)
	{
		//如果右子树被遍历过了
		StackPop(&stack);
		//替换掉原来的打印
		tmp = top->left;
		top->left = top->right;
		top->right = tmp;

		//记录这个被遍历的节点
		last = top;
	}
	else{
		//如果右子树没有被便利过
		cur = top->right;
	}
	}
}

void Test()
{
	int preOrder[] = {1,2,3,-1,4,5,-1,-1,-1,6,-1,-1,7,8,-1,-1,9,-1,10};//按前序遍历
	//int preOrder[] = {1,2,3,-1,-1,4,-1,-1,5,6};
	int size = sizeof(preOrder)/sizeof(preOrder[0]);
	int usedSize;
	BTreeNode *root = CreateTree(preOrder, size, &usedSize);
	int ret;

#if 0
	PreOrder(root);
	printf("\n");

	InOrder(root);
	printf("\n");

	PostOrder(root);
	printf("\n");

	printf("树的节点个数是：%d\n",GetNodeSize(root));
	printf("叶子节点的个数为：%d\n",GetLeafSize(root));
	printf("第三层的节点数为：%d\n",GetLevelKSize(root,3));
	printf("树的深度为：%d\n",GetHeight(root));

	Find(root,9);

	LevelOrder(root);
	printf("\n");

	ret = IsCompleteTree(root);
	if(ret == 1){
		printf("是完全二叉树\n");
	}
	else{
		printf("不是完全二叉树\n");
	}

	PreOrderLoop(root);
	printf("\n");

	InOrderLoop(root);
	printf("\n");

	PostOrderLoop(root);
	printf("\n");

	Mirror1(root);
	Mirror2(root);
#endif

}