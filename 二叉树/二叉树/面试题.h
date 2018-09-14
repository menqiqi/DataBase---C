#pragma once
#include "BTree.h"

//求二叉树中两个节点的最近公共祖先结点
BTreeNode* GetAncestor(BTreeNode *root, BTreeNode *node1, BTreeNode *node2)
{
	//判断node1和node2在哪个子树
	BTreeNode *node1InLeft = Find(root->left,node1->data);
	BTreeNode *node2InLeft = Find(root->left,node2->data);
	BTreeNode *node1InRight = Find(root->right,node1->data);
	BTreeNode *node2InRight = Find(root->right,node2->data);

	//两个节点不在同一个子树上
	if((node1InLeft && node2InRight) || (node1InRight && node2InLeft))
	{
		return root;
	}
	if(node1InLeft)
	{
		return GetAncestor(root->left,node1,node2);
	}
	else
	{
		return GetAncestor(root->right,node1,node2);
	}
}

//判断一棵二叉树是否为平衡二叉树
//1.左右子树都是平衡树
//2.左右子树高度相差不超过1
int IsBalance(BTreeNode* root)
{
	int leftBalance;
	int rightBalance;
	int leftHeight;
	int rightHeight;
	int diff;//高度差
	if(root == NULL)
	{
		return 1;
	}
	//判断左子树是否平衡
	leftBalance = IsBalance(root->left);
	if(!leftBalance){
		//如果左子树不平衡，没必要继续，整棵树一定不平衡
		return 0;
	}
	//判断右子树是否平衡
	rightBalance = IsBalance(root->right);
	if(!rightBalance){
		//如果右子树不平衡，那么这棵树一定不平衡
		return 0;
	}
	leftHeight = GetHeight(root->left);
	rightHeight = GetHeight(root->right);
	//高度相差不超过1
	diff = leftHeight - rightHeight;
	if(diff>=-1 && diff<=1){
		return 1;
	}
	else{
		return 0;
	}
}

//优化
int IsBalanceAd(BTreeNode *root, int *pHeight)
{
	int leftBalance;
	int rightBalance;
	int leftHeight;
	int rightHeight;
	int diff;
	if(root == NULL)
	{
		*pHeight = 0;
		return 1;
	}
	//判断左子树是否平衡
	leftBalance = IsBalanceAd(root->left,&leftHeight);
	if(!leftBalance){
		//左子树不平衡，这棵树一定不平衡
		*pHeight = -1;//不平衡的情况，高度没有用，给一个无意义的值
			return 0;
	}
	//判断右子树是否平衡
	rightBalance = IsBalanceAd(root->right,&rightHeight);
	if(!rightBalance){
		//同上
		*pHeight = -1;
		return 0;
	}
	//真正的整棵树的高度
	*pHeight = MAX(leftHeight,rightHeight)+1;
	//高度差不超过1
	diff = leftHeight - rightHeight;
	if(diff>=-1 && diff<=1){
		return 1;
	}
	else{
		return 0;
	}
}

//求二叉树中最远的两个节点之间的距离
#include<stdio.h>
int MAX_TREE(int a, int b, int c)
{
	if(a>=b && a>=c){
	    return a;
	}
	if(b>=a && b>=c){
		return b;
	}
	return c;
}

//优化掉求高度的过程
int GetLongDistance(BTreeNode *root)
{
	int leftLD;
	int rightLD;
	int leftHeight;
	int rightHeight;
	int rootLD;
	if(root == NULL){
		return 0;
	}                                                                                        
	leftLD = GetLongDistance(root->left);
	rightLD = GetLongDistance(root->right);
	leftHeight = GetHeight(root->left);
	rightHeight = GetHeight(root->right);
	rootLD = leftHeight+rightHeight;
	return MAX_TREE(leftLD, rightLD, rootLD);
}

void Test2()
{
	int preOrder[] = {1,2,3,-1,4,5,-1,-1,-1,6,-1,-1,7,8,-1,-1,9,-1,10};//按前序遍历
	//int preOrder[] = {1,2,3,-1,-1,4,-1,-1,5,6};
	int size = sizeof(preOrder)/sizeof(preOrder[0]);
	int usedSize;
	int pHeight;
	int r;
	BTreeNode *root = CreateTree(preOrder, size, &usedSize);
	
	GetAncestor(root,root->left->left,root->left->right);

	if(IsBalanceAd(root,&pHeight) == 1){
		printf("是平衡树\n");
	}
	else{
		printf("不是平衡树\n");
	}

	r = GetLongDistance(root);
	printf("两个节点之间的最远距离是%d\n",r);
}
