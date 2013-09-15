#include <stdio.h>
#include <string.h>
#include <math.h>




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
	int i, j, k, m;
	int str1_len = strlen(str1), str2_len = strlen(str2);

	// Computation of K'(s,t) will be stored in this.
	// one extra for accounting empty string
	double kDash [str2_len+1] [str1_len+1];
	
	// Initialize kdash with ones
	for(i = 0; i < str2_len+1; i++)
		for(j = 0; j < str1_len+1; j++)
			kDash[i][j] = 1.0;
	

	// This array is to store the values of KDash next.
	double kDashNext [str2_len+1] [str1_len+1];

	// First compute KDashNext upto n-1 using the computed kDash
	for(m = 1; m <= n-1; m++)
		{
		// kDashNext(i,j) = 0 if 0 <= i,j <= l-1
		for(i = 0; i <= str2_len; i++)
			{
			for(j = 0; j <= str1_len; j++)
				{
				if(min(i, j) < m)
					{
					kDashNext[i][j] = 0.0;
					}
				else {
					kDashNext[i][j] = lambda * kDashNext[i][j-1];
					// for every match of ith char in str1 in str2
					for(k = 1; k <= i; k++)
						{
						if(str1[j-1] == str2[k-1])
							{
							kDashNext[i][j] += pow(lambda, i - k + 2) * kDash[k-1][j-1];
							}
						} // loop_k
					}
				} // loop_j
			} // loop_i
		} // loop_m

	// Now use the recursion for computing K
	// Do not declare another array, use (overwrite) kDash only..."
	for(i = 0; i <= str2_len; i++)
		{
		for(j = 0; j <= str1_len; j++)
			{
			if(min(i, j) < n)
				{
				kDashNext[i][j] = 0.0;
				}
			else {
				kDashNext[i][j] = kDashNext[i][j-1];
				// For every match of str1(j) in str2
				for(k = 1; k <= i; k++)
					{
					if( str2[k-1] == str1[j-1])
						{
						kDashNext[i][j] += lambda * lambda * kDash[k-1][j-1];
						}
					} // loop_k
				}
			}	// loop_j
		} // loop_i


	return kDashNext[str2_len][str1_len];
	}



int min(int i, int j)
	{
	if(i < j)
		return i;
	return i;
	}


double normalized_sk(char *s, char *t, double lambda, int n)
	{
	double k_ss = stringkernel(s, s, lambda, n);
	double k_tt = stringkernel(t, t, lambda, n);
	double k_st = stringkernel(s, t, lambda, n);

	double kz = k_st / (sqrt(k_ss * k_tt));
	return kz;
	}


int main(char** argv, int argc)
	{
	char *s = "science is organized knowledge";
	char *t = "wisdom is organized life";

	double val = normalized_sk(s, t, 0.2, 3);
	printf("Val is %f", val);
	}
