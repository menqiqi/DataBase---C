//链式队列
#pragma once

#include<stdio.h>
#include<stdlib.h>
#include<assert.h>

struct BTreeNode;

typedef BTreeNode* QDataType;

typedef struct QueueNode{ //链式队列节点的结构体定义
	QDataType data;
	struct QueueNode* next;
}QueueNode;

typedef struct Queue{ //链式队列的结构体定义
	QueueNode* front;//队头指针
	QueueNode* rear;//队尾指针
}Queue;

//初始化
void QueueInit(Queue *pQueue)
{
	pQueue->front = pQueue->rear = NULL;
	
}

//销毁
void QueueDestroy(Queue *pQueue)
{
	QueueNode* cur;
	QueueNode* next;
	for(cur=pQueue->front; cur!=NULL; cur=next)
	{
		next = cur->next;
		free(cur);
	}
	pQueue->front = pQueue->rear = NULL;
}

static QueueNode* QCreateNode(QDataType data)
{
	QueueNode* newNode = (QueueNode*)malloc(sizeof(QueueNode));
	assert(newNode);
	newNode->data = data;
	newNode->next = NULL;
	return newNode;
}

//增（从队尾入队列）
void QueuePush(Queue *pQueue, QDataType data)
{
	QueueNode* newNode = QCreateNode(data);
	//特殊情况：一个节点都没有
	if(pQueue->front == NULL)
	{
		pQueue->front = pQueue->rear = newNode;
		return;
	}
	//正常情况
	pQueue->rear->next = newNode;
	pQueue->rear = newNode;
}

//删（从对头删除）
void QueuePop(Queue* pQueue)
{
	QueueNode *cur = pQueue->front;
	assert(pQueue != NULL);
	assert(pQueue->front != NULL);
	//特殊情况：只有一个节点
	if(pQueue->front == NULL)
	{
		pQueue->rear = NULL;
		return;
	}
	//正常情况
	pQueue->front = cur->next;
	free(cur);
}

//是否为空
int IsEmpty(const Queue* pQueue)
{
	return pQueue->front == NULL;
}

//查
QDataType QueueTop(Queue* pQueue)
{
	assert(pQueue != NULL);//地址不为空
	assert(pQueue->front != NULL);//队列为空，不支持
	return pQueue->front->data;
}

//返回数据个数
int QueueSize(const Queue* pQueue)
{
	int size = 0;
	QueueNode* cur;
	for(cur=pQueue->front; cur!=NULL; cur=cur->next)
	{
       size++;
	}
	return size;
}
