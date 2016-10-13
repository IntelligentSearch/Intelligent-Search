jflex query.flex
javac Query.java
input="./input.txt"
while IFS= read -r var
do
  echo $var
  echo $var > in1.txt
  java Query in1.txt
done < "$input"

