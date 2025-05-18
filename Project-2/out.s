.text
main:
  move $fp, $sp
  addi $sp, $sp, -432
  li $t2, 0
  li $v0, 5
  syscall
  move $t3, $v0
  addi $t1, $n, $0
  bgt  $t3, $t4, return
  addi $t3, $t3, -{rhs}
  sub $t3, $t3, 1
  li $t5, 0
loop0
  bgt  $t5, $t3, exit0
  li $v0, 5
  syscall
  move $t2, $v0
  sll $t1, $t5, 2
  add $t0, $t6, $t1
  sw , 0($t0)
  addi $t1, $t5, $0
  add , $t5, $t1
  j    loop0
exit0
  jal 
  li $t5, 0
loop1
  bgt  $t5, $t3, exit1
  sll $t1, $t5, 2
  add $t0, $t6, $t1
  lw $t2, 0($t0)
  jal 
  jal 
  addi $t1, $t5, $0
  add , $t5, $t1
  j    loop1
exit1
return

quicksort:
  move $fp, $sp
  addi $sp, $sp, -864
  li $t2, 0
  li $t3, 0
  bge  $t4, $t5, end
  add , $t4, $t5
  addi $t1, $t6, $0
  div  , $t6, $t1
  sll $t1, $t6, 2
  add $t0, $t7, $t1
  lw $t8, 0($t0)
  addi $t2, $t4, -{rhs}
  sub $t2, $t4, 1
  addi $t1, $t5, $0
  add , $t5, $t1
loop0
loop1
  addi $t1, $t2, $0
  add , $t2, $t1
  sll $t1, $t2, 2
  add $t0, $t7, $t1
  lw $t9, 0($t0)
  li $t10, $t9
  blt  $t10, $t8, loop1
loop2
  addi $t3, $t3, -{rhs}
  sub $t3, $t3, 1
  sll $t1, $t3, 2
  add $t0, $t7, $t1
  lw $t9, 0($t0)
  li $t11, $t9
  bgt  $t11, $t8, loop2
  bge  $t2, $t3, exit0
  sll $t1, $t3, 2
  add $t0, $t7, $t1
  sw , 0($t0)
  sll $t1, $t2, 2
  add $t0, $t7, $t1
  sw , 0($t0)
  j    loop0
exit0
  addi $t1, $t3, $0
  add , $t3, $t1
  jal 
  addi $t1, $t3, $0
  add , $t3, $t1
  jal 
end

