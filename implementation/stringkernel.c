#include <stdio.h>
#include <string.h>

/* Computes string kernel between two given strings 
	
	Parameters:
		- str1: string one
		- str2: string two
		- lambda: decaying factor in (0, 1)
		- n: substring length
	
	Returns: string kernel computed between str1 and str2
 */
double stringkernel(char *str1, char *str2, double lambda, int n)
	{
	int i, j;
	

	for(i = 0; i < strlen(str1); i++)
		printf("Char %d = %c", i, str1[i]);
	return 1.0;
	}


int main(char** argv, int argc)
	{
	printf("Hello");
	float val = stringkernel("hello", "dear", 0.2, 2);
	printf("Val is %f", val);
	}
