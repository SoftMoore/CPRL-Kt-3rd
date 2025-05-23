   CALL _main
   HALT
_makeRectangleFromPoints:
   PROC 16
   LDLADDR 8
   LDLADDR -16
   LOAD 8
   STORE 8
   LDLADDR 8
   LDCINT 8
   ADD
   LDLADDR -8
   LOAD 8
   STORE 8
   LDLADDR -32
   LDLADDR 8
   LOAD 16
   STORE 16
   RET 16
_writelnPoint:
   LDCSTR "Point("
   PUTSTR 6
   LDLADDR -8
   LOADW
   PUTINT
   LDCSTR ", "
   PUTSTR 2
   LDLADDR -8
   LDCINT 4
   ADD
   LOADW
   PUTINT
   LDCSTR ")"
   PUTSTR 1
   PUTEOL
   RET 8
_writelnRectangle:
   LDCSTR "Rectangle(Point("
   PUTSTR 16
   LDLADDR -16
   LOADW
   PUTINT
   LDCSTR ", "
   PUTSTR 2
   LDLADDR -16
   LDCINT 4
   ADD
   LOADW
   PUTINT
   LDCSTR "), Point("
   PUTSTR 9
   LDLADDR -16
   LDCINT 8
   ADD
   LOADW
   PUTINT
   LDCSTR ", "
   PUTSTR 2
   LDLADDR -16
   LDCINT 8
   ADD
   LDCINT 4
   ADD
   LOADW
   PUTINT
   LDCSTR "))"
   PUTSTR 2
   PUTEOL
   RET 16
_main:
   PROC 48
   LDLADDR 8
   LDCINT 1
   LDCINT 2
   STORE 8
   LDLADDR 16
   LDCINT 5
   LDCINT 8
   STORE 8
   LDLADDR 24
   LDCINT 1
   LDCINT 2
   LDCINT 5
   LDCINT 8
   STORE 16
   LDLADDR 8
   LOAD 8
   CALL _writelnPoint
   LDLADDR 16
   LOAD 8
   CALL _writelnPoint
   LDLADDR 24
   LOAD 16
   CALL _writelnRectangle
   LDLADDR 40
   ALLOC 16
   LDLADDR 8
   LOAD 8
   LDLADDR 16
   LOAD 8
   CALL _makeRectangleFromPoints
   STORE 16
   LDLADDR 40
   LOAD 16
   CALL _writelnRectangle
   RET 0
