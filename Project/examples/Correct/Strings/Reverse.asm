   CALL _main
   HALT
_main:
   PROC 164
   LDLADDR 8
   LDCSTR "12345"
   STORE 14
   LDCSTR "Reversing \""
   PUTSTR 11
   LDLADDR 8
   LOAD 164
   PUTSTR 80
   LDCSTR "\": "
   PUTSTR 3
   LDLADDR 8
   CALL _reverse
   LDLADDR 8
   LOAD 164
   PUTSTR 80
   PUTEOL
   RET 0
_reverse:
   PROC 10
   LDLADDR 8
   LDCINT 0
   STOREW
   LDLADDR 12
   LDLADDR -4
   LOADW
   LOADW
   LDCINT 1
   SUB
   STOREW
L2:
   LDLADDR 8
   LOADW
   LDLADDR 12
   LOADW
   BGE L3
   LDLADDR 16
   LDLADDR -4
   LOADW
   LDCINT 4
   ADD
   LDLADDR 8
   LOADW
   LDCINT 2
   MUL
   ADD
   LOAD2B
   STORE2B
   LDLADDR -4
   LOADW
   LDCINT 4
   ADD
   LDLADDR 8
   LOADW
   LDCINT 2
   MUL
   ADD
   LDLADDR -4
   LOADW
   LDCINT 4
   ADD
   LDLADDR 12
   LOADW
   LDCINT 2
   MUL
   ADD
   LOAD2B
   STORE2B
   LDLADDR -4
   LOADW
   LDCINT 4
   ADD
   LDLADDR 12
   LOADW
   LDCINT 2
   MUL
   ADD
   LDLADDR 16
   LOAD2B
   STORE2B
   LDLADDR 8
   LDLADDR 8
   LOADW
   LDCINT 1
   ADD
   STOREW
   LDLADDR 12
   LDLADDR 12
   LOADW
   LDCINT 1
   SUB
   STOREW
   BR L2
L3:
   RET 4
