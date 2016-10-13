package com.rest;
import java.lang.StringBuilder;
import java.util.Calendar;
%%

%public
%class Query

%unicode
%standalone
%line

%{
  StringBuilder sb = new StringBuilder();
  Calendar cal = Calendar.getInstance();
  String[] days_of_week = {"sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday"};
%}
%%

wiley|earhart|meredith|ford|hillenbrand|windsor { 
sb.append("DINING_COURT=");
sb.append(yytext() + " ");
}


monday|tuesday|wednesday|thursday|friday|saturday|sunday|tomorrow|today {
String in = yytext();
sb.append("MEAL_DAY=");
int current = cal.get(cal.DAY_OF_WEEK);
int desired = 0;
for(int i = 0; i < 7; i++) {
	if(in.equals(days_of_week[i])) {
		desired = i + 1;
		break;
	}
}
int days_to_add = (desired - current);
if(days_to_add < 0) {
	days_to_add += 7;
}
if(in.equals("tomorrow")) days_to_add = 1;
if(in.equals("today")) days_to_add = 0;
cal.add(Calendar.DATE, days_to_add);
String month = String.format("%02d", cal.get(Calendar.MONTH)+1);
String day = String.format("%02d", cal.get(Calendar.DATE));
String year = Integer.toString(cal.get(Calendar.YEAR));
String date = String.format("%s-%s-%s", month, day, year);
sb.append(date + " ");
}

lunch|dinner|breakfast|late\ lunch {
sb.append("MEAL_TIME=");
sb.append(yytext() + " " );
}

<<EOF>>   {
System.out.println(sb.toString());
return 0;
}

[^]       {
	
}
