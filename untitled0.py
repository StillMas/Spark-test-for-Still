# -*- coding: utf-8 -*-
"""
Created on Mon May 29 00:15:05 2017

@author: zqlhust
"""

a=[1,2,3,2,3,5,1,8,9]
b=12
n=len(a)
def fun(a,b):
    result1=[]
    result2=[]
    for i in range(1,n):
        k=n-i+1
        for j in range(1,k):
            s=0
            for k in range(1,i):
                s+=a[k]
                if (s>b):
                    c=str(str("amount of addends:")+str(i))
                    d=str(str("the most close sum:")+str(s))
                    result1=result1.append(c)
                    result2=result2.append(d)
                    
    return(result1[1],result2[1]) 
                
            
    