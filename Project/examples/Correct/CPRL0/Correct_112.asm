   PROGRAM 8
   CALL _main
   HALT
_main:
   LDCB 1
   NOT
   PUTBYTE
   PUTEOL
   LDCB 0
   NOT
   PUTBYTE
   PUTEOL
   LDGADDR 0
   LDCINT 5
   STOREW
   LDGADDR 4
   LDCINT 2
   LDGADDR 0
   LOADW
   MUL
   LDCINT 1
   SUB
   STOREW
L2:
   LDGADDR 0
   LOADW
   LDCINT 2
   LDCINT 5
   MUL
   BG L3
   LDGADDR 0
   LDGADDR 0
   LOADW
   LDCINT 1
   ADD
   STOREW
   LDGADDR 0
   LOADW
   LDCINT 2
   MOD
   LDCINT 0
   BNE L10
   LDCSTR "even"
   PUTSTR 4
   PUTEOL
   BR L11
L10:
   LDGADDR 0
   LOADW
   LDCINT 2
   MOD
   LDCINT 1
   BNE L8
   LDCSTR "odd"
   PUTSTR 3
   PUTEOL
   BR L9
L8:
   LDCSTR "weird"
   PUTSTR 5
   PUTEOL
L9:
L11:
   LDGADDR 0
   LOADW
   LDCINT 9
   BE L3
   BR L2
L3:
   RET 0
