import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;
/* 
 *
 * StringSubsequenceKernel: computes StringKernel (refer, "Text Classification using String Kernels", by
 * Huma Lodhi et. al. for more info) between two strings.
 *
 * Author: 	Prashant Ullegaddi
 * Mail:		prashant.ullegaddi@research.iiit.ac.in
 *
 * DETAILS:
 * Command line options: $java StringSubsequenceKernel string1 string2 lambda n
 * 	where	string1 and string2 are the strings between which the kernel needs to be computed,
 * 			lambda is a decaying factor (a real number)  with 0 < lambda < 1
 * 			n is an integer with n <= min( length(string1), length(string2) )
 */
public class StringSubsequenceKernel
	{
	public static void main( String[] args ) throws Exception
		{
		// Wrong no. of command line options...
		if( args.length != 4 )
			{
			String errorMsg = "USAGE: ";
			errorMsg += "]$java " + StringSubsequenceKernel.class.getName() + " TextFile1 TextFile2 lambda n\n";
			errorMsg += "where\tTextFile1 and TextFile2 are the strings between which the kernel needs to be computed,\n";
			errorMsg += "\tlambda is a decaying factor (a real number)  with 0 < lambda < 1\n";
			errorMsg += "\tn is an integer with n <= min(length(TextFile1),length(TextFile2))\n";
		
			System.err.println( errorMsg );
			System.exit(-1);
			}

		// Read the files, and convert them 
		String str1 = readFile( args[0] );
		String str2 = readFile( args[1] );
		float lambda = (float) Double.parseDouble( args[2] );
		int n = Integer.parseInt( args[3] );

		// Compute the Kernel
		long start = new Date().getTime();
		float kernel12 =  new StringKernel( str1, str2, lambda, n ).computeStringKernel();
		float kernel11 =  new StringKernel( str1, str1, lambda, n ).computeStringKernel();
		float kernel22 =  new StringKernel( str2, str2, lambda, n ).computeStringKernel();
		long end = new Date().getTime();

		System.out.println( kernel12 + " " + kernel11 + " " + kernel22 );
		// Normalize the kernel
		double normalizedKernel = kernel12 / ( Math.sqrt( kernel11 * kernel22 ) ) * 100;

		System.out.println( "Kernel = " + normalizedKernel );
		System.out.println( "Time took = " + ((end-start)/1000.0) + "secs." );
		
		} // end method main
	
	// Reads a file, returns the string representation of the same
	public static String readFile( String file )
		{
		StringBuffer content = new StringBuffer();
		try {
			BufferedReader reader = new BufferedReader( new FileReader( file ) );
			String line = null;
			while( (line = reader.readLine()) != null )
				{
				content.append( line.trim() );
				}
			}
		catch( IOException ioe )
		 	{}

		return content.toString();
		} // end method readFile

	} // end class StringSubsequenceKernel



// Actual implementation of String Kernel between two strings
class StringKernel
	{
	// The two strings to be compared for similarity by computing String Kernel between them.
	private String str1;
	private String str2;

	// The decay factor
	private float lambda;

	// The length of subsequences
	private int n;

	// Computation of K'(s,t) will be stored in this.
	float[][] kDash = null;
	
	// Constructor
	public StringKernel( String str1, String str2, float lambda, int n )
		{
		this.str1 = str1;
		this.str2 = str2;
		this.lambda = lambda;
		this.n = n;

		// initialize kDash, one extra for accounting empty string
		kDash = new float [str2.length()+1][str1.length()+1];

		// Initialize it with 1's
		for( int i = 0; i < str2.length()+1; i++ )
			{
			for( int j = 0; j < str1.length()+1; j++ )
				{
				kDash[i][j] = (float)1.0;
				}
			}				
		} // end ctor

	
	// Computes K(str1, str2)
	public float computeStringKernel()
		{
		// This array is to store the values of KDash next.
		float[][] kDashNext = new float [str2.length()+1][str1.length()+1];

		// First compute KDashNext upto n-1 using the computed kDash
		for( int l = 1; l <= n-1; l++ )
			{
			// kDashNext(i,j) = 0 if 0 <= i,j <= l-1
			for( int i = 0; i <= str2.length(); i++ )
				{
				for( int j = 0; j <= str1.length(); j++ )
					{
					if( Math.min(i, j) < l )
						kDashNext[i][j] = (float)0.0;
					else
						{
						kDashNext[i][j] = lambda * kDashNext[i][j-1];
						// for every match of ith char in str1 in str2
						for( int k = 1; k <= i; k++ )
							{
							if( str1.charAt(j-1) == str2.charAt(k-1) )
								{
								kDashNext[i][j] += Math.pow(lambda, i - k + 2) * kDash[k-1][j-1]; 
								}
							} // loop_k
						}
					} // loop_j
				} // loop_i

			
			// Make this kDashNext as kDash for next loop
			for( int i = 0; i <= str2.length(); i++ )
				{
				for( int j = 0; j <= str1.length(); j++ )
					{
					kDash[i][j] = kDashNext[i][j];
					}
				}

			System.out.println( "Done with l = " + l );
			} // loop_l

			System.out.println( "Done computing K'" );

			// Now use the recursion for computing K
			// Do not declare another array, use (overwrite) kDash only...
			for( int i = 0; i <= str2.length(); i++ )
				{
				for( int j = 0; j <= str1.length(); j++ )
					{
					
					if( Math.min(i,j) < n )
						{
						kDashNext[i][j] = (float)0.0;
						}
					else
						{
						kDashNext[i][j] = kDashNext[i][j-1];
						// For every match of str1(j) in str2
						for( int k = 1; k <= i; k++ )
							{
//							System.out.println( "stucke here" );
							if( str2.charAt(k-1) == str1.charAt(j-1) )
								{
								kDashNext[i][j] += lambda * lambda * kDash[k-1][j-1];
								}
							} // loop_k
						}
					} // loop_j
				} // loop_i
		System.out.println( "Done" );
		return kDashNext[str2.length()][str1.length()];
		} // end method computeStringKernel


	// Prints the matrix
	private void printMatrix( float[][] matrix )
		{
		for( int i = 0; i < matrix.length; i++ )
			{
			for( int j = 0; j < matrix[0].length; j++ )
				{
				System.out.print( "\t" + matrix[i][j] );
				}

			System.out.println( );
			}
		}
	} // end class StringKernel
