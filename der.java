package lab1;

import java.rmi.server.Operation;
import java.util.Scanner;
import java.util.regex.Pattern;


class Mul_exp {
	int coe;
	int ver_num;
	int ver_kind;
	int[] ver_pow=new int[26];
	Mul_exp(){
		coe=1;
		ver_num=ver_kind=0;
		for(int i=0;i<ver_pow.length;i++)
		{
			ver_pow[i]=0;
		}
	}
		void add_ver(int p){
			if((ver_kind>>p & 1)==1)
			{
				ver_num+=0;
			}
			else
			{
				ver_num+=1;
			}
			ver_kind =ver_kind | 1<<p;
			ver_pow[p]++;
		}
		

		int getcoe(){
			return coe;
		}
		
		void add_coe(int x){
			coe+=x;
		}
		
		void build(String str1,int t){			//解析乘法段 
			coe=1;
			ver_num=ver_kind=0;
			char[] str=str1.toCharArray();
			for(int i=0;i<ver_pow.length;i++)
			{
				ver_pow[i]=0;
			}
			int p = 0,temp=0,k = 0;
			boolean flag=false; 
			for(int i=0;i<str.length;i++){
				if(str[i]>= 'a' && str[i]<='z'){
					p=str[i]-'a';
					add_ver(p);
				}
				else if(str[i]>= '0' && str[i]<='9'){
					if(temp==0)
						k=i-1;
					flag=true;
					temp=temp*10+str[i]-'0';
				}
				if(flag){
					if(k==-1)
					{
						coe*=temp;
						temp=0;
					}
					else{
						if(str[k]=='*'){
							coe*=temp;
							temp=0;
						}
						else if(str[k]=='^'){
							ver_pow[p]+=temp-1; 
							temp=0;
						}
					}
					flag=false;
				}
			}	
			if(flag){
				if(k<0)
				{
					coe*=temp;
				}
				else
				{
					if(str[k]=='*' || k<0)
						coe*=temp;
					else if(str[k]=='^')
						ver_pow[p]+=temp-1;
				}
			}
			coe*=t;
		}
		
		void print(){
			int kind=ver_kind, p=0;
			boolean flag=false;
			if(coe!=0)
					System.out.printf("%d", coe);
			while(kind>0){
				if((kind&1)==1){
					for(int j=1;j<=ver_pow[p];j++)
					{
						System.out.printf("*%c",p+'a');
					}
					flag=true;
				}
				kind>>=1;
				p++;
			}

		}
		int pow(int x,int n){
			int a=1;
			while(n>0){
				if((n&1)==1)
					a*=x;
				x*=x;
				n>>=1;
			}
			return a;
		}
		void Ass(int p, int x){
			if((ver_kind>>p &1)==1){
				ver_num--;
				ver_kind-=1<<p;
			}
			coe*=pow(x,ver_pow[p]);
			ver_pow[p]=0;
		}
		
		void Der(int p)
		{
			if((ver_kind>>p & 1)==1)
				coe*= ver_pow[p];
			else
			{
				coe=0;
			}
			if(--ver_pow[p]==0){
				ver_kind-=1<<p;
				ver_num--;
			}
		}

		boolean operator(Mul_exp a,Mul_exp b){
			if(a.ver_num!=b.ver_num && a.ver_kind!=b.ver_kind) return false;
			for(int i=0;i<26;i++)
	    		if(a.ver_pow[i]!=b.ver_pow[i])
	    			return false;
	    	return true;
		}
        
}
public class der{
static int Cut(String str1, Mul_exp S[]){		//解析多项式 获得单项 
	
	String mul="";
	char[] str=str1.toCharArray();
	int len=str.length;
	int count=0;
	int t=1;
	for(int i=0;i<len;i++){
		if(str[i]=='+' || str[i]=='-'){
			S[++count].build(mul,t);
			mul="";
			t= str[i]=='+'?1:-1;
		} 
		else
			mul+=str[i];
	}
	S[++count].build(mul,t);
	return count;
}

static void Merge(Mul_exp T[], int size){			//合并同类项 
	for(int i=1;i<size;i++)
		if(T[i].getcoe()!=0)
			for(int j=i+1;j<=size;j++)
				if(T[j].getcoe()!=0)
					if(T[i].operator(T[i],T[j])){			//判断是否同类项 
						T[i].add_coe(T[j].getcoe());
						T[j].coe=0;

					}
}

static void Print(Mul_exp T[], int size)
{
	boolean flag=false;
	int i;
	for(i=1;i<=size;i++)					//打印第一项 
		if(T[i].getcoe()>0){
			flag=true;
			T[i].print();
			break;
		}
	for(++i;i<=size;i++)				//打印其他项 
		if(T[i].getcoe()!=0){
			if(T[i].getcoe()>0)
				System.out.printf("+");
			T[i].print();
		}
	if(!flag)
		System.out.printf("0");
	System.out.printf("\n");
}


static void simplify(String str1, Mul_exp T[],int size){
	char[] str=str1.toCharArray();
	int len=str.length,i=10,temp=0,k=10;
	while(i<len){
		if(str[i]>='0' && str[i]<='9'){
			temp=temp*10+str[i]-'0';
		}
		else if(str[i]==' '){
			for(int j=1;j<=size;j++)
				T[j].Ass(str[k]-'a', temp);
			temp=0;
			k=i+1;
		}
		++i;
	}
	for(int j=1;j<=size;j++)
		T[j].Ass(str[k]-'a', temp);
}

static void derivative(String str1, Mul_exp T[],int size){
	char[] str=str1.toCharArray();
	for(int j=1;j<=size;j++)
		T[j].Der(str[4]-'a');
}
static void copy(Mul_exp[] S,Mul_exp[] T,int size)
{
	for(int i=1;i<=size;i++)
	{
		T[i].coe=S[i].coe;
		T[i].ver_kind=S[i].ver_kind;
		T[i].ver_num=S[i].ver_num;
		for(int j=0;j<26;j++)
		{
			T[i].ver_pow[j]=S[i].ver_pow[j];
		}
	}
}
public static void main(String[] args){
	String str="";
	String command1="";
	int exp_num = 0;
//	Mul_exp S[maxN],T[maxN]; 
	final Mul_exp[] S=new Mul_exp[20];
	Mul_exp[] T=new Mul_exp[20];
	for(int i=1;i<20;i++)
	{
		S[i]=new Mul_exp();
		T[i]=new Mul_exp();
	}  
	Scanner in=new Scanner(System.in);
	double pre=0,fin = 0;
	while(true){
		command1=in.nextLine();
		pre=System.currentTimeMillis();
		char[] command=command1.toCharArray();
		if(command[0]=='!'){
			if(command1.startsWith("!simplify")){
				copy(S,T,exp_num);
				simplify(command1,T,exp_num);
				Merge(T,exp_num);
				Print(T,exp_num);
				fin=System.currentTimeMillis();
			}
			else if(command1.startsWith("!d/d")){
				copy(S,T,exp_num);
				derivative(command1,T,exp_num);
				Merge(T,exp_num);
				Print(T,exp_num);
				fin=System.currentTimeMillis();
			}
		}
		else{
			str=command1;
			exp_num=Cut(str, S);
			Merge(S,exp_num);
			Print(S,exp_num); 
			fin=System.currentTimeMillis();
	}
		
	} 
	}
}
 