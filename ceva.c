#include <stdio.h>
#include <stdlib.h>
#include "Profiler.h"

Profiler profiler("demo");
//profiler.countOperation("k-merge", nrElem, 1);
using namespace std;

struct TreeNode {
	TreeNode *left;
	TreeNode *right;
	int height;
	int data;
	int size;
};

// folosim santinela ca e simplu si fain
struct TreeNode* nodNou(int data) {
	struct TreeNode *node = new TreeNode();
	node->data = data;
	node->size = 1; 
	node->height = 1;
	node->left = NULL;
	node->right = NULL;
	return node;
}

int OS_Rank(struct TreeNode *root, struct TreeNode* x);

/**
* Calculeaza campul size dupa o relatie simpla de recurenta
*/
int sizeOf(struct TreeNode *node) {
	int size;
	if (node == NULL)
		return 0;
	size = sizeOf(node->left) + sizeOf(node->right) + 1;
	return size;
}

// O functie utilitara
int height(struct TreeNode *N)
{
	if (N == NULL)
		return 0;
	return N->height;
}

int maxx(int a, int b)
{
	return (a > b) ? a : b;
}


/**
* Functii de rotatie la stanga si la dreapta 
* Necesare dupa stergeri la rebalansare O(1)
*/
struct TreeNode* rotatieDreapta(struct TreeNode *y) {
	struct TreeNode *x = y->left;
	struct TreeNode *T2 = x->right;

	// Efectuam rotatia...schimbarea la pointeri
	x->right = y;
	y->left = T2;

	// Actualizarea inaltimilor
	y->height = max(height(y->left), height(y->right)) + 1;
	x->height = max(height(x->left), height(x->right)) + 1;
	return x;
}

struct TreeNode *rotatieStanga(struct TreeNode *x) {
	struct TreeNode *y = x->right;
	struct TreeNode *T2 = y->left;

	// facem rotatia
	y->left = x;
	x->right = T2;

	x->size = sizeOf(x);
	y->size = sizeOf(y);
	return y;
}

/**
* Functia care o folosim pe post de inaltime inversa
* E de fapt chiar gradul unui nod
*/
//int OS_Rank(struct TreeNode *root, struct TreeNode* x) {
//	int r = x->left->size + 1;
//	struct TreeNode* y;
//	y = x;
//	while (y != root) {
//		if (y == y->parinte->right)
//			r = r + y->parinte->left->size + 1;
//		y = y->parinte;
//	}
//	return r;
//}

/**
* Vedem daca arborele este intr-adevar echilibrat
*/
int getBalance(struct TreeNode *root, struct TreeNode *N) {
	if (N == NULL)
		return 0;
	return height(N->left) - height(N->right);
}

struct TreeNode * nodMinim(struct TreeNode* node)
{
	struct TreeNode* current = node;

	/* trebuie sa il gasim pe cel mai mic */
	while (current->left != NULL)
		current = current->left;

	return current;
}
/**
* Functie folosita pentru stergerea unui nod
*/
struct TreeNode *OS_Delete(struct TreeNode* root, int data) {
	if (root == NULL)
		return root;

	// Daca cheia ce o vrem stearsa e mai mica decat cheia radacinii 
	// mergem in subarborele stang
	if (data < root->data)
		root->left = OS_Delete(root->left, data);
	// Aceeasi idee ca mai sus daca e mai mare
	else if (data > root->data)
		root->right = OS_Delete(root->right, data);
	// cheia e identica cu cea a radacinii
	else {
		if ((root->left == NULL) || (root->right == NULL)) {
			struct TreeNode *temp = root->left ? root->left : root->right;

			// nu-s fii
			if (temp == NULL) {
				temp = root;
				root = NULL;
			}
			else
				*root = *temp; // pune acel copil
			free(temp);
		}
		else {
			// nod cu doi copii (ial pe succesor inordine)
			struct TreeNode *temp = nodMinim(root->right);

			// copiem datele acelui succesor
			root->data = temp->data;
			
			// stergem acel succesor
			root->right = OS_Delete(root->right, temp->data);
			free(temp);
		}
	}

	// Daca avea un singur nod return
	if (root == NULL)
		return root;

	int balance = getBalance(root, root);

	// stanga-stanga
	if (balance > 1 && getBalance(root, root->left) >= 0) 
		return rotatieDreapta(root);
	
	// stanga dreapta
	if (balance > 1 && getBalance(root, root->left) < 0) {
		root->left = rotatieStanga(root->left);
		return rotatieDreapta(root);
	}

	// dreapta dreapta
	if (balance < -1 && getBalance(root, root->right) <= 0)
		return rotatieStanga(root);

	// dreapta stanga
	if (balance < -1 && getBalance(root, root->right) > 0) {
		root->right = rotatieDreapta(root->right);
		return rotatieStanga(root);
	}

	return root;
}

/**
* Functie de generare arbore AVL din vector sortat
*/
struct TreeNode* arraySortatInBST(int arr[], int start, int end, struct TreeNode* parinte = NULL) {
	if (start > end)
		return NULL;
	
	// Ia primul element
	int mid = (start + end) / 2;
	struct TreeNode *root = nodNou(arr[mid]);
	root->parinte = parinte; // setam parintele...pentru radacina va fi chiar NULL si e OK

	// constructie recursiva pentru ramura stanga si dreapta
	root->left = arraySortatInBST(arr, start, mid - 1, root);
	root->right = arraySortatInBST(arr, mid + 1, end, root);

	root->size = sizeOf(root);
	return root;
}


/**
* Functii de afisare a arborilor - pre/in/postordine
*/
void preOrder(struct TreeNode* node) {
	if (node == NULL)
		return;
	printf("%d  ", node->data);
	preOrder(node->left);
	preOrder(node->right);
}

void inOrder(struct TreeNode *node) {
	if (node == NULL)
		return;
	inOrder(node->left);
	printf("%d  ", node->data);
	inOrder(node->right);
}

void prettyPrint(struct TreeNode *node, int indent) {
	if (node == NULL)
		return;
	indent += 4;
	prettyPrint(node->right, indent);
	printf("%*c", indent, ' ');
	printf("%d\n", node->data);
	prettyPrint(node->left, indent);
}

/**
* Selectia cu statistica de ordine
*/
struct TreeNode* OS_Select(struct TreeNode *node, int i) {
	int k = node->left->size + 1;
	if (i == k)
		return node;
	if (i < k)
		return OS_Select(node->left, i);
	else
		return OS_Select(node->right, i - k);
}
   
int main() {
	int arr[] = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 };
	int n = sizeof(arr) / sizeof(arr[0]);

	/* Parte de DEMO */
	struct TreeNode *root = arraySortatInBST(arr, 0, n - 1);
	printf("\n Traversarea in preordine a arborelui: ");
	preOrder(root);
	printf("\n Traversarea in inOrdine a arborelui: ");
	inOrder(root);
	printf("\n");
	prettyPrint(root, 0);

	root = OS_Delete(root, 10);
	printf("\nAm sters pe 10 \n");
	prettyPrint(root, 0);
	return 0;
}