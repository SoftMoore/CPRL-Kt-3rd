   CALL _main
   HALT
_main:
   PROC 20
   LDLADDR 8
   ALLOC 20
   CALL _makeArray
   STORE 20
   LDLADDR 8
   CALL _printArray
   RET 0
_makeArray:
   PROC 20
   LDLADDR 8
   LDCINT 0
   LDCINT 1
   LDCINT 2
   LDCINT 3
   LDCINT 4
   STORE 20
   LDLADDR -20
   LDLADDR 8
   LOAD 20
   STORE 20
   RET 0
_printArray:
   PROC 4
   LDLADDR 8
   LDCINT 0
   STOREW
L0:
   LDLADDR 8
   LOADW
   LDCINT 5
   LDCINT 1
   SUB
   BG L1
   LDLADDR -4
   LOADW
   LDLADDR 8
   LOADW
   LDCINT 4
   MUL
   ADD
   LOADW
   PUTINT
   LDCSTR "  "
   PUTSTR 2
   LDLADDR 8
   LDLADDR 8
   LOADW
   LDCINT 1
   ADD
   STOREW
   BR L0
L1:
   PUTEOL
   RET 4
