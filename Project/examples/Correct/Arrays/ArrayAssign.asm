   CALL _main
   HALT
_main:
   PROC 88
   LDLADDR 8
   LDCINT 0
   LDCINT 1
   LDCINT 2
   LDCINT 3
   LDCINT 4
   STORE 20
   LDLADDR 48
   LDCINT 5
   LDCINT 4
   LDCINT 3
   LDCINT 2
   LDCINT 1
   STORE 20
   LDLADDR 28
   LDLADDR 8
   LOAD 20
   STORE 20
   LDLADDR 28
   LDCINT 0
   LDCINT 4
   MUL
   ADD
   LOADW
   PUTINT
   LDLADDR 88
   LDCINT 1
   STOREW
L0:
   LDLADDR 88
   LOADW
   LDCINT 4
   BG L1
   LDCSTR ", "
   PUTSTR 2
   LDLADDR 28
   LDLADDR 88
   LOADW
   LDCINT 4
   MUL
   ADD
   LOADW
   PUTINT
   LDLADDR 88
   LDLADDR 88
   LOADW
   LDCINT 1
   ADD
   STOREW
   BR L0
L1:
   PUTEOL
   LDLADDR 68
   LDLADDR 48
   LOAD 20
   STORE 20
   LDLADDR 68
   LDCINT 0
   LDCINT 4
   MUL
   ADD
   LOADW
   PUTINT
   LDLADDR 92
   LDCINT 1
   STOREW
L2:
   LDLADDR 92
   LOADW
   LDCINT 4
   BG L3
   LDCSTR ", "
   PUTSTR 2
   LDLADDR 68
   LDLADDR 92
   LOADW
   LDCINT 4
   MUL
   ADD
   LOADW
   PUTINT
   LDLADDR 92
   LDLADDR 92
   LOADW
   LDCINT 1
   ADD
   STOREW
   BR L2
L3:
   PUTEOL
   RET 0
