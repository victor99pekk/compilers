quicksort:
 move $fp, $sp
 addi $sp, $sp, -856
MOVE $i, 0
MOVE $j, 0
BGE  $lo, $hi, end
ADD $mid, $lo, $hi
DIV  $mid $mid, 2
LW   $pivot, mid($A)
SUB $i, $lo, 1
ADD $j, $hi, 1
loop0:
loop1:
ADD $i, $i, 1
LW   $x, i($A)
MOVE $ti, $x
BLT  $ti, $pivot, loop1
loop2:
SUB $j, $j, 1
LW   $x, j($A)
MOVE $tj, $x
BGT  $tj, $pivot, loop2
BGE  $i, $j, exit0
SW   $ti, j($A)
SW   $tj, i($A)
J    loop0
exit0:
ADD $j1, $j, 1
JAL  quicksort
ADD $j, $j, 1
JAL  quicksort
end:

main:
 move $fp, $sp
 addi $sp, $sp, -424
MOVE $t, 0
JAL  geti
MOVE $n, $v0
BGT  $n, 100, return
SUB $n, $n, 1
MOVE $i, 0
loop0:
BGT  $i, $n, exit0
JAL  geti
MOVE $t, $v0
SW   $t, i($A)
ADD $i, $i, 1
J    loop0
exit0:
JAL  quicksort
MOVE $i, 0
loop1:
BGT  $i, $n, exit1
LW   $t, i($A)
JAL  puti
JAL  putc
ADD $i, $i, 1
J    loop1
exit1:
return:

