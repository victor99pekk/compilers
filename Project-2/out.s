.text
main:
  move $fp, $sp
  addi $sp, $sp, -432
li $v0, 9
li $a0, 400
syscall
sw $v0, -8($fp)
  li $t0, 0
  sw $t0, -12($fp)
  addi $sp, $sp, -4
  sw $v0, 0($sp)
  li $v0, 5
  syscall
  sw $v0, -20($fp)
  lw $v0, 0($sp)
  addi $sp, $sp, 4
  lw $t1, -20($fp)
  li $t2, 100
  brgt $t1, $t2, return_main
  li $t2, 1
  lw $t1, -20($fp)
  sub $t0, $t1, $t2
  sw $t0, -20($fp)
  li $t0, 0
  sw $t0, -16($fp)
loop0_main:
  lw $t1, -16($fp)
  lw $t2, -20($fp)
  brgt $t1, $t2, exit0_main
  addi $sp, $sp, -4
  sw $v0, 0($sp)
  li $v0, 5
  syscall
  sw $v0, -12($fp)
  lw $v0, 0($sp)
  addi $sp, $sp, 4
  lw $t1, -16($fp)
  lw $t2, -8($fp)
  sll $t1, $t1, 2
  add $t1, $t1, $t2
  lw $t0, -12($fp)
  sw , 0($t1)
  li $t2, 1
  lw $t1, -16($fp)
  add $t0, $t1, $t2
  sw $t0, -16($fp)
  j loop0_main
exit0_main:
  lw $a0, -8($fp)
  li $a1, 0
  lw $a2, -20($fp)
  addi $sp, $sp, -8
  sw $fp, 0($sp)
  sw $ra, 4($sp)
  jal quicksort
  lw $fp, 0($sp)
  lw $ra, 4($sp)
  addi $sp, $sp, 8
  li $t0, 0
  sw $t0, -16($fp)
loop1_main:
  lw $t1, -16($fp)
  lw $t2, -20($fp)
  brgt $t1, $t2, exit1_main
  lw $t1, -16($fp)
  lw $t2, -8($fp)
  sll $t1, $t1, 2
  add $t1, $t1, $t2
  lw $t0, 0($t1)
  sw $t0, -12($fp)
  addi $sp, $sp, -8
  sw $v0, 4($sp)
  sw $a0, 0($sp)
  li $v0, 1
  lw $a0, -12($fp)
  syscall
  lw $a0, 0($sp)
  lw $v0, 4($sp)
  addi $sp, $sp, 8
  addi $sp, $sp, -8
  sw $v0, 4($sp)
  sw $a0, 0($sp)
  li $v0, 11
  li $a0, 10
  syscall
  lw $a0, 0($sp)
  lw $v0, 4($sp)
  addi $sp, $sp, 8
  li $t2, 1
  lw $t1, -16($fp)
  add $t0, $t1, $t2
  sw $t0, -16($fp)
  j loop1_main
exit1_main:
return_main:

quicksort:
  move $fp, $sp
  addi $sp, $sp, -864
li $v0, 9
li $a0, 400
syscall
sw $v0, -20($fp)
  sw $a0, -20($fp)
  sw $a1, -12($fp)
  sw $a2, -16($fp)
  li $t0, 0
  sw $t0, -48($fp)
  li $t0, 0
  sw $t0, -52($fp)
  lw $t1, -12($fp)
  lw $t2, -16($fp)
  brgeq $t1, $t2, end_quicksort
  lw $t2, -16($fp)
  lw $t1, -12($fp)
  add $t0, $t1, $t2
  sw $t0, -40($fp)
  li $t2, 2
  lw $t1, -40($fp)
  div $t0, $t1, $t2
  sw $t0, -40($fp)
  lw $t1, -40($fp)
  lw $t2, -20($fp)
  sll $t1, $t1, 2
  add $t1, $t1, $t2
  lw $t0, 0($t1)
  sw $t0, -44($fp)
  li $t2, 1
  lw $t1, -12($fp)
  sub $t0, $t1, $t2
  sw $t0, -48($fp)
  li $t2, 1
  lw $t1, -16($fp)
  add $t0, $t1, $t2
  sw $t0, -52($fp)
loop0_quicksort:
loop1_quicksort:
  li $t2, 1
  lw $t1, -48($fp)
  add $t0, $t1, $t2
  sw $t0, -48($fp)
  lw $t1, -48($fp)
  lw $t2, -20($fp)
  sll $t1, $t1, 2
  add $t1, $t1, $t2
  lw $t0, 0($t1)
  sw $t0, -36($fp)
  lw $t1, -36($fp)
  move $t0, $t1
  sw $t0, -24($fp)
  lw $t1, -24($fp)
  lw $t2, -44($fp)
  brlt $t1, $t2, loop1_quicksort
loop2_quicksort:
  li $t2, 1
  lw $t1, -52($fp)
  sub $t0, $t1, $t2
  sw $t0, -52($fp)
  lw $t1, -52($fp)
  lw $t2, -20($fp)
  sll $t1, $t1, 2
  add $t1, $t1, $t2
  lw $t0, 0($t1)
  sw $t0, -36($fp)
  lw $t1, -36($fp)
  move $t0, $t1
  sw $t0, -28($fp)
  lw $t1, -28($fp)
  lw $t2, -44($fp)
  brgt $t1, $t2, loop2_quicksort
  lw $t1, -48($fp)
  lw $t2, -52($fp)
  brgeq $t1, $t2, exit0_quicksort
  lw $t1, -52($fp)
  lw $t2, -20($fp)
  sll $t1, $t1, 2
  add $t1, $t1, $t2
  lw $t0, -24($fp)
  sw , 0($t1)
  lw $t1, -48($fp)
  lw $t2, -20($fp)
  sll $t1, $t1, 2
  add $t1, $t1, $t2
  lw $t0, -28($fp)
  sw , 0($t1)
  j loop0_quicksort
exit0_quicksort:
  li $t2, 1
  lw $t1, -52($fp)
  add $t0, $t1, $t2
  sw $t0, -32($fp)
  lw $a0, -20($fp)
  lw $a1, -12($fp)
  lw $a2, -52($fp)
  addi $sp, $sp, -8
  sw $fp, 0($sp)
  sw $ra, 4($sp)
  jal quicksort
  lw $fp, 0($sp)
  lw $ra, 4($sp)
  addi $sp, $sp, 8
  li $t2, 1
  lw $t1, -52($fp)
  add $t0, $t1, $t2
  sw $t0, -52($fp)
  lw $a0, -20($fp)
  lw $a1, -52($fp)
  lw $a2, -16($fp)
  addi $sp, $sp, -8
  sw $fp, 0($sp)
  sw $ra, 4($sp)
  jal quicksort
  lw $fp, 0($sp)
  lw $ra, 4($sp)
  addi $sp, $sp, 8
end_quicksort:

