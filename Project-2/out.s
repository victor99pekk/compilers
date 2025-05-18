.text
main:
  move $fp, $sp
  addi $sp, $sp, -432
  lw $t, 0
  JAL  geti
  MOVE $n, $v0
  addi $temp1, $n, $0
  bgt  $n, $temp1, return
  addi $n, $n, 1
  lw $i, 0
loop0
  bgt  $i, $n, exit0
  JAL  geti
  MOVE $t, $v0
  sll $temp1, $i, 2
  add $temp0, $A, $temp1
  sw , 0($temp0)
  addi $temp1, $i, $0
  add , $i, $temp1
  j    loop0
exit0
  jal 
  lw $i, 0
loop1
  bgt  $i, $n, exit1
  sll $temp1, $i, 2
  add $temp0, $A, $temp1
  lw $t, 0($temp0)
  jal 
  jal 
  addi $temp1, $i, $0
  add , $i, $temp1
  j    loop1
exit1
return

quicksort:
  move $fp, $sp
  addi $sp, $sp, -864
  lw $i, 0
  lw $j, 0
  bge  $lo, $hi, end
  add , $lo, $hi
  addi $temp1, $mid, $0
  div   $mid, $temp1
  sll $temp1, $mid, 2
  add $temp0, $A, $temp1
  lw $pivot, 0($temp0)
  addi $i, $lo, 1
  addi $temp1, $hi, $0
  add , $hi, $temp1
loop0
loop1
  addi $temp1, $i, $0
  add , $i, $temp1
  sll $temp1, $i, 2
  add $temp0, $A, $temp1
  lw $x, 0($temp0)
  lw $ti, $x
  blt  $ti, $pivot, loop1
loop2
  addi $j, $j, 1
  sll $temp1, $j, 2
  add $temp0, $A, $temp1
  lw $x, 0($temp0)
  lw $tj, $x
  bgt  $tj, $pivot, loop2
  bge  $i, $j, exit0
  sll $temp1, $j, 2
  add $temp0, $A, $temp1
  sw , 0($temp0)
  sll $temp1, $i, 2
  add $temp0, $A, $temp1
  sw , 0($temp0)
  j    loop0
exit0
  addi $temp1, $j, $0
  add , $j, $temp1
  jal 
  addi $temp1, $j, $0
  add , $j, $temp1
  jal 
end

