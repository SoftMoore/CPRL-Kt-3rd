   CALL _main
   HALT
_main:
   PROC 88
   LDLADDR 8
   LDCSTR "Bill"
   STORE 12
   LDCSTR "name1 is "
   PUTSTR 9
   LDLADDR 8
   LOAD 44
   PUTSTR 20
   PUTEOL
   LDLADDR 52
   LDLADDR 8
   LOAD 44
   STORE 44
   LDCSTR "name2 is "
   PUTSTR 9
   LDLADDR 52
   LOAD 44
   PUTSTR 20
   PUTEOL
   RET 0
