public class test
{

	public static boolean inBounds(int row, int col)
	{	return false;
	}

	public static int get()
	{
		return 1;
	}

	public static void main(String[] args)
	{	
		int a = get();
		int b = 1;
		if(a>0 || a <0) a = 0;
		if(a == 1 && b == 1) inBounds(a,b);
		return;
	}
}
