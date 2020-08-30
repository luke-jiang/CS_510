

## Question 1
C, C++ and Java output -1, but Perl and Python output 1.

The reason for this discrepancy is because different languages have different
implementations

Strategies:
  * compiler: generates
C/C++:  (output -1)
```
#include <stdio.h>

int main()
{
    printf("%i", (-5) % 2);

    return 0;
}
```
Java:    (output -1)
```
public class HelloWorld{

     public static void main(String []args){
        System.out.println((-5) % 2);
     }
}
```
Python:    (output 1)
```
print((-5) % 2)
```
Perl:     (output 1)
```
print ((-5) % 2);
```

## Question 2

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
