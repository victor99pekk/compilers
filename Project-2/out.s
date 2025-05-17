.text
quicksort:
  move $fp, $sp
  addi $sp, $sp, -864
  end:
  addi $(sp), $0, 
  jal quicksort
  add $j, $j, 1
  addi $(sp), $0, 
  jal quicksort
  add $j1, $j, 1
  exit0:
  j    loop0
  addi $arr, $A, $0
  sll $dst, {base}
  addi $t0, {lhs}, {src}
  sub $t0, {lhs}, {src}
  sw $dst, {offset}$base
  addi $offset, $i, $0
  addi $arr, $A, $0
  sll $dst, {base}
  addi $t0, {lhs}, {src}
  sub $t0, {lhs}, {src}
  sw $dst, {offset}$base
  addi $offset, $j, $0
  bge  $i, $j, exit0
  BGT  $tj, $pivot, loop2
  lw $tj, $x
  addi $arr, $A, $0
  sll $dst, {base}
  addi $t0, {lhs}, {src}
  sub $t0, {lhs}, {src}
  sw $dst, {offset}$base
  addi $offset, $j, $0
  addi $j, $j, 1
  loop2:
  BLT  $ti, $pivot, loop1
  lw $ti, $x
  addi $arr, $A, $0
  sll $dst, {base}
  addi $t0, {lhs}, {src}
  sub $t0, {lhs}, {src}
  sw $dst, {offset}$base
  addi $offset, $i, $0
  add $i, $i, 1
  loop1:
  loop0:
  add $j, $hi, 1
  addi $i, $lo, 1
  addi $arr, $A, $0
  sll $dst, {base}
  addi $t0, {lhs}, {src}
  sub $t0, {lhs}, {src}
  sw $dst, {offset}$base
  addi $offset, $mid, $0
  DIV  $mid $mid, 2
  add $mid, $lo, $hi
  bge  $lo, $hi, end
  lw $j, 0
  lw $i, 0

main:
  move $fp, $sp
  addi $sp, $sp, -432
  return:
  exit1:
  j    loop1
  add $i, $i, 1
  addi $(sp), $0, 
  jal putc
  addi $(sp), $0, 
  jal puti
  addi $arr, $A, $0
  sll $dst, {base}
  addi $t0, {lhs}, {src}
  sub $t0, {lhs}, {src}
  sw $dst, {offset}$base
  addi $offset, $i, $0
  BGT  $i, $n, exit1
  loop1:
  lw $i, 0
  addi $(sp), $0, 
  jal quicksort
  exit0:
  j    loop0
  add $i, $i, 1
  addi $arr, $A, $0
  sll $dst, {base}
  addi $t0, {lhs}, {src}
  sub $t0, {lhs}, {src}
  sw $dst, {offset}$base
  addi $offset, $i, $0
  JAL  geti
  MOVE $t, $v0
  BGT  $i, $n, exit0
  loop0:
  lw $i, 0
  addi $n, $n, 1
  addi $temp1, $n, $0
  BGT  $n, $temp1, return
  JAL  geti
  MOVE $n, $v0
  lw $t, 0

