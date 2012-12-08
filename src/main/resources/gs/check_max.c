#include<stdio.h>
int main(){
    FILE *fp = fopen("trecgen06.passage","r");
    int qId,docId,begin,end;
    int max = 0;
    int count = 0;
    int count500 = 0;
    int count1000 = 0;
    int count2000 = 0;
    double sum = 0.0;
    char aspect[1024];
    char buffer[2048];
    //while(fscanf(fp,"%d %d %d %d %s",&qId,&docId,&begin,&end,aspect) > 0){
    while( fgets(buffer,sizeof(buffer),fp) != NULL){
//        printf("%s\n",buffer);
        sscanf(buffer,"%d %d %d %d %*s",&qId,&docId,&begin,&end);
        if( end - begin > max ){
            max = end - begin;
        }
        if(end - begin > 500)
            count500++;
        if(end - begin > 1000)
            count1000++;
        if(end - begin > 1500)
            count2000++;
        printf("begin:%d end:%d\n",begin,end);
        sum += (end- begin);
        count++;
//        if(count > 10)
//            break;
    }
    printf("count:%d max is %d mean:%lf count500:%lf count1000:%lf count2000:%lf\n",count,max,sum/count,(double)count500/count,(double)count1000/count,(double)count2000/count);
}
