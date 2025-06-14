grammar Exp;

   options {
     language = Java;
   }

   start
     :  def EOF
     ;

   def : (AND? base)+
       |  (OR? '(' def ')')*
       ;

   base : key operator values ;

   key : ID ;

   values : LSQR  VALUE   (','  VALUE)* RSQR  ;

   operator : IN
            | NIN
            ;
   VALUE:  '"' .*? '"' ;
   AND : 'AND' ;
   OR : 'OR' ;
   NOT : 'not' ;
   EQ : '=' ;
   COMMA : ',' ;
   SEMI : ';' ;
   IN : 'IN' ;
   NIN : 'NOT IN' ;
   LSQR : '[' ;
   RSQR : ']' ;


   INT : [0-9]+ ;
   ID: [a-zA-Z_][a-zA-Z_0-9\-!]* ;
   WS: [\t\n\r\f ]+ -> skip ;
