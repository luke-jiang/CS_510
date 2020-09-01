

## Question 1
C, C++ and Java output -1, but Perl and Python output 1.

The reason for this discrepancy is because different languages have different
implementations

Strategies:
  * compiler: generates
C/C++:  (output -1)
```C
#include <stdio.h>

int main()
{
    printf("%i", (-5) % 2);

    return 0;
}
```
Java:    (output -1)
```Java
public class HelloWorld{

     public static void main(String []args){
        System.out.println((-5) % 2);
     }
}
```
Python:    (output 1)
```Python
print((-5) % 2)
```
Perl:     (output 1)
```Perl
print ((-5) % 2);
```

## Question 2
Valgrind Output:
```
==184638==
==184638== HEAP SUMMARY:
==184638==     in use at exit: 9 bytes in 1 blocks
==184638==   total heap usage: 7 allocs, 6 frees, 2,115 bytes allocated
==184638==
==184638== LEAK SUMMARY:
==184638==    definitely lost: 9 bytes in 1 blocks
==184638==    indirectly lost: 0 bytes in 0 blocks
==184638==      possibly lost: 0 bytes in 0 blocks
==184638==    still reachable: 0 bytes in 0 blocks
==184638==         suppressed: 0 bytes in 0 blocks
==184638== Rerun with --leak-check=full to see details of leaked memory
==184638==
==184638== For counts of detected and suppressed errors, rerun with: -v
==184638== ERROR SUMMARY: 0 errors from 0 contexts (suppressed: 0 from 0)
```
(a) The problem is that when the program exits, not all allocated memories are freed. The bug for test case 1 is that when a node is to be deleted from the linked list in function delete_node, the str field of that node is not freed. Therefore, I fixed it by freeing the str field before freeing the node

(b) The problem is that there are some invalid pointer dereference as well as some invalid address input for the free function inside delete_all and i. The bug for this case is that the pointer p is not updated to NULL after all nodes in the list are freed, so when delete_all is called consecutively 2 times, in the second time, it will point to a block that is already freed.

(c) The following test case would generate an error when run with Valgrind:
```
[(i)nsert,(d)elete,delete (a)ll,d(u)plicate,(e)dit,(p)rint,e(x)it]:i
enter the tel:>100
enter the name:>Tom

[(i)nsert,(d)elete,delete (a)ll,d(u)plicate,(e)dit,(p)rint,e(x)it]:u
enter the tel:>100

[(i)nsert,(d)elete,delete (a)ll,d(u)plicate,(e)dit,(p)rint,e(x)it]:e
```

Valgrind would report:
```
==52345== Invalid write of size 1
==52345==    at 0x4C32E0D: strcpy (in /usr/lib/valgrind/vgpreload_memcheck-amd64-linux.so)
==52345==    by 0x108F39: duplicate (sll_fixed.c:193)
==52345==    by 0x1092FA: main (sll_fixed.c:320)
==52345==  Address 0x522d973 is 0 bytes after a block of size 3 alloc'd
==52345==    at 0x4C2FB0F: malloc (in /usr/lib/valgrind/vgpreload_memcheck-amd64-linux.so)
==52345==    by 0x108F1F: duplicate (sll_fixed.c:192)
==52345==    by 0x1092FA: main (sll_fixed.c:320)
```

The bug is at the malloc call of function duplicate. The variable len is the length of the str field without counting the null character, but the size of char array to allocate should include the size of the null character. Therefore, changing the malloc size from len to (len + 1) fixes the bug.


## Question 4:
(a)  
TR for NC: {1,2,3,4,5,6,7,8,9,10,11}

TR for EC: {  
   [1,2],[1,3],  
   [2,3],  
   [3,4],[3,5],[3,6],[3,7],  
   [4,8],  
   [5,8],  
   [6,7],  
   [7,8],  
   [8,9],[8,10],  
   [9,11],  
   [10,11]  
}

TR for EPC: {  
   [1,2,3],  
   [1,3,4],[1,3,5],[1,3,5],[1,3,7],  
   [2,3,4],[2,3,5],[2,3,6],[2,3,7],  
   [3,4,8],  
   [3,5,8],  
   [3,6,7],[3,6,8],  
   [3,7,8],  
   [4,8,9],[4,8,10],  
   [5,8,9],[5,8,10],  
   [6,8,9],[6,8,10],  
   [7,8,9],[7,8,10],  
   [8,9,11],  
   [8,10,11]  
}


   

