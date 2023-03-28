<template>
  <div>
    <!--    面包屑导航区域-->
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item :to="{ path: '/welcome' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>日程管理</el-breadcrumb-item>
      <el-breadcrumb-item>周报写作</el-breadcrumb-item>
    </el-breadcrumb>
    <!--    卡片视图区域-->
    <el-card shadow="hover">
      <el-form
        :model="ruleForm"
        :rules="rules"
        ref="ruleFormRef"
        label-width="80px"
        class="demo-ruleForm"
      >
        <el-form-item label="开始日期" required>
          <el-col :span="11">
            <!-- el-date-picker组件获取的日期格式  通过format转换成string格式标准的年月日 value-format="yyyy-MM-dd"  format="yyyy-MM-dd"-->
            <el-form-item prop="date">
              <el-date-picker
                type="date"
                placeholder="选择开始日期"
                v-model="ruleForm.date"
                style="width: 50%"
                value-format="yyyy-MM-dd"
                :picker-options="pickerOptions"
                format="yyyy-MM-dd"
              >
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-form-item>

        <el-form-item label="结束日期" required>
          <el-col :span="11">
            <el-form-item prop="ddl">
              <el-date-picker
                type="date"
                placeholder="选择结束日期"
                v-model="ruleForm.ddl"
                style="width: 50%"
                value-format="yyyy-MM-dd"
                :picker-options="pickerOptionsDdl"
                format="yyyy-MM-dd"
              >
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-form-item>

        <el-form-item label="文献阅读">
          <el-transfer
            :titles="['所有文献', '我的阅读']"
            filterable
            filter-placeholder="请输入文献名称"
            v-model="targetList"
            :data="paperList"
            @change="logg"
          >
          </el-transfer>
        </el-form-item>

        <el-form-item label="已完成" prop="finished" required>
          <!-- :rows="5"控制textarea的大小（高度）   -->
          <el-input
            type="textarea"
            v-model="ruleForm.finished"
            maxlength="300"
            :rows="5"
            show-word-limit
            placeholder="请填写时间段内已完成的任务和收获，如已阅读文献的主题，模型、方法，实验等"
          >
          </el-input>
        </el-form-item>

        <el-form-item label="待完成" prop="unfinished" required>
          <!-- :rows="5"控制textarea的大小（高度）   -->
          <el-input
            type="textarea"
            v-model="ruleForm.unfinished"
            maxlength="300"
            :rows="5"
            show-word-limit
            placeholder="请填写接下来计划完成的任务"
          >
          </el-input>
        </el-form-item>

        <el-form-item>
          <el-button round type="primary" @click="outPort">导出</el-button>
          <el-button round @click="saveForm">保存</el-button>
          <el-button round @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
export default {
  created() {
    this.$axios
      .get("/user/groupId?userId=" + window.sessionStorage.getItem("userId"))
      .then((res) => {
        if (res.data.data === null) {
          this.$message.warning({
            message: "请先加入研究组！",
            duration: 1500,
            showClose: true,
          });
          this.$router.push("/allGroups");
        } else {
          this.groupId = res.data.data;
          this.id = window.sessionStorage.getItem("userId");
          this.userType = window.sessionStorage.getItem("userType");
          this.generatePaperList();
          this.getWeeklyWritingInfo();
        }
      })
      .catch((err) => {
        this.$message.error({ message: err, duration: 1500, showClose: true });
      });

  },
  data() {
    return {
      id: "",
      groupId: 0,
      userType: "",
      ruleForm: {
        date: "",
        finished: "",
        unfinished: "",
        ddl: "",
      },
      rules: {
        date: [
          { required: true, message: "开始日期不能为空", trigger: "blur" },
        ],
        finished: [
          {
            required: true,
            message: "已完成的任务不得为空",
            trigger: "blur",
          },
        ],
        unfinished: [
          {
            required: true,
            message: "计划完成的任务不得为空",
            trigger: "blur",
          },
        ],
        ddl: [{ required: true, message: "结束日期不能为空", trigger: "blur" }],
      },
      paperList: [],
      targetList: [],
      //禁止选择当前日期之后的日期
      pickerOptions: {
        disabledDate(v) {
          return v.getTime() >= new Date().getTime() - 86400000;
        },
      },
      pickerOptionsDdl: {
        //禁止选择开始日期之前的日期
        disabledDate: (time) => {
          if (this.ruleForm.date) {
            return time.getTime() < new Date(this.ruleForm.date).getTime() - 86400000;
          } else {
            return time.getTime() < new Date().getTime() - 86400000;
          }
        },
      },
    };
  },
  methods: {
    logg(){
      console.log(this.targetList)
    },
    //文献列表数据生成
    generatePaperList() {
      this.$axios
        .post(
          "/search/searchdocinfo?currentPage=" +
            0 +
            "&keyword=" +
            null +
            "&pageSize=" +
            25+
            "&type=" +
            0 +
            "&searcherid=" +
            this.id +
            "&direction=" +
            null
        )
        .then((res) => {
          let originList = res.data.data;
          //console.log(originList);
          if (originList !== "") {
            originList.forEach((paper, index) => {
              this.paperList.push({
                label: paper.title,
                key: index,
              });
            });
          }
        })
        .catch((err) => {
          this.$message.error({
            message: err,
            duration: 1500,
            showClose: true,
          });
        });
    },
    //导出
    outPort() {
      let saveContext={};
      //saveContext["docs"]=this.targetList;
      saveContext["docs"]=[];
      for(var i=0;i<this.targetList.length;i++){
        saveContext["docs"].push(this.paperList[this.targetList[i]].label);
      }
      saveContext["docs_str"]="["+saveContext["docs"].toString()+"]"
      saveContext["done"]=this.ruleForm.finished;
      saveContext["endDate"]=this.ruleForm.ddl;
      saveContext["gid"]=this.groupId;
      saveContext["startDate"]=this.ruleForm.date;
      saveContext["todo"]=this.ruleForm.unfinished;
      saveContext["uid"]=this.id;
      //this.$axios.post("/weekly/download",saveContext).then(res=>{
        //console.log(res)
      //})
      //this.$axios.get(
          //`/weekly/download?uid=${saveContext["uid"]}&gid=${saveContext["gid"]}&startDate=${saveContext["startDate"]}&endDate=${saveContext["endDate"]}&docs=${saveContext["docs_str"]}&done=${saveContext["done"]}&todo=${saveContext["todo"]}`)
      //.then(res=>{
        //console.log(res)
      //})
      window.open(`http://42.193.37.120:9712/weekly/download?uid=${saveContext["uid"]}&gid=${saveContext["gid"]}&startDate=${saveContext["startDate"]}&endDate=${saveContext["endDate"]}&docs=${saveContext["docs_str"]}&done=${saveContext["done"]}&todo=${saveContext["todo"]}`);


    },
    //重置表单
    resetForm() {
      this.$refs.ruleFormRef.resetFields();
      this.paperList = [];
      this.targetList = [];
      this.generatePaperList();
    },
    saveForm(){
      let saveContext={};
      saveContext["docs"]=this.targetList;
      saveContext["done"]=this.ruleForm.finished;
      saveContext["endDate"]=this.ruleForm.ddl;
      saveContext["gid"]=this.groupId;
      saveContext["startDate"]=this.ruleForm.date;
      saveContext["todo"]=this.ruleForm.unfinished;
      saveContext["uid"]=this.id;

      console.log(saveContext)
      this.$axios.post("/weekly/save",saveContext).then(res=>{
        console.log(res)
      })
    },

    getWeeklyWritingInfo(){
      this.$axios.get("/weekly/get?uid="+this.id+
          "&gid="+this.groupId).then(res=>{
            //console.log(res.data.data)
        if(res.data.data.docs!=null){
          //this.targetList.push(16)
          this.targetList=res.data.data.docs;
          for(var i=0;i<this.targetList.length;i++){
            this.targetList[i]=parseInt(this.targetList[i]);
          }
          console.log(this.paperList);
          console.log(this.targetList)
        }
        if(res.data.data.done!=null){
          this.$data.ruleForm.finished=res.data.data.done;
        }
        if(res.data.data.todo!=null){
          this.$data.ruleForm.unfinished=res.data.data.todo;
        }
        if(res.data.data.endDate!=null){
          this.$data.ruleForm.ddl=res.data.data.endDate;
        }
        if(res.data.data.startDate!=null){
          this.$data.ruleForm.date=res.data.data.startDate;
        }
      })
    }
  },
};
</script>

<style scoped lang="less">
</style>