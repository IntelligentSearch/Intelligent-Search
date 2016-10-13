import java.lang.StringBuilder;
import java.util.Calendar;
%%

%public
%class Query
%unicode
%type String
%standalone
%ignorecase
%{
  boolean today = true;
  StringBuilder sb = new StringBuilder();
  StringBuilder itemBuild = new StringBuilder();
  String fin = "";
  String getString() {
	return fin;
  }
  Calendar cal = Calendar.getInstance();
  String[] days_of_week = {"sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday"};
%}

DiningCourt = wiley|earhart|meredith|ford|hillenbrand|windsor
DayWeek = monday|tuesday|wednesday|thursday|friday|saturday|sunday|tomorrow|today  
MealTime = lunch|dinner|breakfast|late\ lunch  
Erase = be|have|for|what|whats|there|to|eat|where|at|is|what's|can|on|will|get|i|any|
TrashWords = (have|for|at|there|{MealTime}|{DayWeek}|{DiningCourt})
SigWord = !({TrashWords}|\ )
%%

{Erase}  {
}
/* {TrashWords}\ {SigWord}\ {TrashWords} {
	String match = yytext();
	sb.append("ITEM_NAME=");
	String item = match.substring(match.indexOf(" ")+1,match.lastIndexOf(" "));
	sb.append(item + " ");
	yypushback(match.substring(match.lastIndexOf(" ")).length());
} */

{DiningCourt} { 
sb.append("DINING_COURT=");
sb.append(yytext() + " ");
}


{DayWeek} {
today = false;
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

{MealTime} {
sb.append("MEAL_TIME=");
sb.append(yytext() + " " );
}

[a-zA-Z]+ {
	itemBuild.append(yytext() + " ");
}	

<<EOF>>   {
if(today) {
	String date = String.format("%02d-%02d-%d",cal.get(Calendar.MONTH)+1, cal.get(Calendar.DATE), cal.get(Calendar.YEAR));
	sb.append("MEAL_DAY=" + date + " ");
}
String item = itemBuild.toString();
if(!item.equals("")) sb.append("ITEM_NAME="+item+" ");
String fin = sb.toString();
System.out.println(fin);
return fin;
}


[^]       {
	
}
