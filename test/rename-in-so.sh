
inputFile=libunity.so
outputFile=output.so

#准备查找和替换字符串的字节值表示,两个字符串必须一样长
searchBytes=$(printf "Landroid/app/Activity;" | xxd -p -c0)
replacementBytes=$(printf "Ltshadow/app/Activity;" | xxd -p -c0)

#待替换字符串长度
stringLength=$((${#searchBytes}/2))

#xxd输出字节文本时换行长度,这个长度必须大于stringLength*2-2,
#以保证被替换待字符串如果在第一个字符和最后一个字符都出现被换行截断都情况时，
#我们通过给文件前面增加stringLength-1长度的字节，可以把这些被换行截断的字符串都推到下一行中且不会再被截断。
#因此这些被截断都待替换字符串可以在第二次替换中被正确匹配到。
columnLength=100

#原始文件输出到round1.txt
xxd -c $columnLength -p $inputFile > round1.txt

#macOS下安装gsed以便和GNU sed保持一致
#查找并替换字节
gsed -i "s/$searchBytes/$replacementBytes/g" round1.txt

#还原round1.txt到so文件
xxd -r -c $columnLength -p round1.txt > round1.so

#虽然前面添加stringLength-1长度字节就够了，但这里省事点，直接添加searchBytes本身
echo $searchBytes > prepend-round1.txt
xxd -c $stringLength -p round1.so >> prepend-round1.txt
xxd -r -c $stringLength -p prepend-round1.txt > prepend-round1.so

#prepend-round1.so输出到round2.txt
xxd -c $columnLength -p prepend-round1.so> round2.txt

#第二轮查找并替换字节
gsed -i "s/$searchBytes/$replacementBytes/g" round2.txt

#还原round2.txt到so文件
xxd -r -c $columnLength -p round2.txt > prepend-round2.so

#去掉前面多余的字节
xxd -c $stringLength -p prepend-round2.so > prepend-round2.txt
gsed -i '1d' prepend-round2.txt
xxd -r -c $stringLength -p prepend-round2.txt > $outputFile

#清理中间文件
rm -f round1.so
rm -f round1.txt
rm -f round2.txt
rm -f prepend-round1.txt
rm -f prepend-round1.so
rm -f prepend-round2.so
rm -f prepend-round2.txt

