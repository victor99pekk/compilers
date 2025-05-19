.text
main:
  move $fp, $sp
  addi $sp, $sp, -56
  li $t0, 0
  sw $t0, -20($fp)
  addi $sp, $sp, -4
  sw $v0, 0($sp)
  li $v0, 6
  syscall
  sw $f0, -32($fp)
  lw $v0, 0($sp)
  addi $sp, $sp, 4
  li $t0, $0.000001
  sw $t0, -24($fp)
  lw $t1, -32($fp)
  move $t0, $t1
  sw $t0, -12($fp)
  lw $t1, -12($fp)
  move $t0, $t1
  sw $t0, -16($fp)
  li $t0, $0.
  sw $t0, -8($fp)
  li $t0, $0.
  sw $t0, -28($fp)
  li $t0, $0.
  sw $t0, -40($fp)
  lw $t1, -32($fp)
  li $t2, $0.
  brlt $t1, $t2, EXIT_main
LABEL0_main:
  lw $t2, -12($fp)
  lw $t1, -12($fp)
  mult $t0, $t1, $t2
  sw $t0, -8($fp)
  lw $t2, -32($fp)
  lw $t1, -8($fp)
  sub $t0, $t1, $t2
  sw $t0, -36($fp)
  lw $t1, -36($fp)
  move $t0, $t1
  sw $t0, -28($fp)
  lw $t1, -28($fp)
  li $t2, $0.
  brgeq $t1, $t2, LABEL1_main
  lw $t2, -28($fp)
  li $t1, $0.
  sub $t0, $t1, $t2
  sw $t0, -36($fp)
  lw $t1, -36($fp)
  move $t0, $t1
  sw $t0, -28($fp)
LABEL1_main:
  lw $t1, -28($fp)
  lw $t2, -24($fp)
  brleq $t1, $t2, LABEL2_main
  lw $t1, -12($fp)
  move $t0, $t1
  sw $t0, -16($fp)
  lw $t2, -12($fp)
  lw $t1, -32($fp)
  div $t0, $t1, $t2
  sw $t0, -40($fp)
  lw $t2, -12($fp)
  lw $t1, -40($fp)
  add $t0, $t1, $t2
  sw $t0, -40($fp)
  li $t2, $2.
  lw $t1, -40($fp)
  div $t0, $t1, $t2
  sw $t0, -40($fp)
  lw $t1, -40($fp)
  move $t0, $t1
  sw $t0, -12($fp)
  li $t2, 1
  lw $t1, -20($fp)
  add $t0, $t1, $t2
  sw $t0, -20($fp)
  lw $t1, -16($fp)
  lw $t2, -12($fp)
  breq $t1, $t2, LABEL2_main
  j LABEL0_main
LABEL2_main:
  addi $sp, $sp, -8
  sw $v0, 4($sp)
  sw $f12, 0($sp)
  li $v0, 2
  lw $f12, -12($fp)
  syscall
  lw $f12, 0($sp)
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
EXIT_main:

