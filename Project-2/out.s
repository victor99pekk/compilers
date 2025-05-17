quicksort:
  move $fp, $sp
  addi $sp, $sp, -864
  lw $i, 0
  lw $j, 0
  bge  $lo, $hi, end
  add $mid, $lo, $hi
  DIV  $mid $mid, 2
  addi $arr, $0, $A
  addi $offset, $0, $mid
  la $rt, {base}
  LW   $pivot, mid($A)
  addi $i, $lo, 1
  add $j, $hi, 1
  loop0:
  loop1:
  add $i, $i, 1
  addi $arr, $0, $A
  addi $offset, $0, $i
  la $rt, {base}
  LW   $x, i($A)
  lw $ti, $x
  BLT  $ti, $pivot, loop1
  loop2:
  addi $j, $j, 1
  addi $arr, $0, $A
  addi $offset, $0, $j
  la $rt, {base}
  LW   $x, j($A)
  lw $tj, $x
  BGT  $tj, $pivot, loop2
  bge  $i, $j, exit0
  SW   $ti, j($A)
  SW   $tj, i($A)
  j    loop0
  exit0:
  add $j1, $j, 1
  jal quicksort
  add $j, $j, 1
  jal quicksort
  end:

main:
  move $fp, $sp
  addi $sp, $sp, -432
  lw $t, 0
  JAL  geti
  MOVE $n, $v0
  addi $reg2, $0, $n
  BGT  $n, $reg2, return
  addi $n, $n, 1
  lw $i, 0
  loop0:
  BGT  $i, $n, exit0
  JAL  geti
  MOVE $t, $v0
  SW   $t, i($A)
  add $i, $i, 1
  j    loop0
  exit0:
  jal quicksort
  lw $i, 0
  loop1:
  BGT  $i, $n, exit1
  addi $arr, $0, $A
  addi $offset, $0, $i
  la $rt, {base}
  LW   $t, i($A)
  jal puti
  jal putc
  add $i, $i, 1
  j    loop1
  exit1:
  return:

