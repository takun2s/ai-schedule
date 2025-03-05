import{d as $,_ as z,s as L,f as w,o as A,c as j,e as b,g as F,a as t,w as o,r,h as q,u as h,i as J,b as V,j as u,t as N,E as c,k as K}from"./index-Cdn4KJAj.js";import{t as f}from"./index-BuPkrTmK.js";const O=$("task",{state:()=>({tasks:[],loading:!1,total:0}),actions:{async fetchTasks(n){this.loading=!0;try{const{data:e}=await f.getTasks(n);this.tasks=e.items,this.total=e.total}catch(e){throw console.error("获取任务列表失败:",e),e}finally{this.loading=!1}},async createTask(n){try{await f.createTask(n)}catch(e){throw console.error("创建任务失败:",e),e}},async updateTask(n,e){try{await f.updateTask(n,e)}catch(_){throw console.error("更新任务失败:",_),_}},async deleteTask(n){try{await f.deleteTask(n)}catch(e){throw console.error("删除任务失败:",e),e}},async startTask(n){try{await f.startTask(n)}catch(e){throw console.error("启动任务失败:",e),e}},async stopTask(n){try{await f.stopTask(n)}catch(e){throw console.error("停止任务失败:",e),e}}}}),Q={class:"tasks"},W={class:"toolbar"},X={class:"dialog-footer"},Y={__name:"Tasks",setup(n){const e=O(),{tasks:_,loading:C}=L(e),d=w(!1),k=w("create"),s=w({name:"",type:"HTTP",cron:"",config:""});A(async()=>{await e.fetchTasks()});const x=()=>{k.value="create",s.value={name:"",type:"HTTP",cron:"",config:""},d.value=!0},U=i=>{k.value="edit",s.value={...i},d.value=!0},I=async()=>{try{k.value==="create"?(await e.createTask(s.value),c.success("创建成功")):(await e.updateTask(s.value.id,s.value),c.success("更新成功")),d.value=!1,await e.fetchTasks()}catch{c.error("操作失败")}},B=async i=>{try{i.status==="RUNNING"?(await e.stopTask(i.id),c.success("已停止")):(await e.startTask(i.id),c.success("已启动")),await e.fetchTasks()}catch{c.error("操作失败")}},E=i=>{K.confirm("确定要删除该任务吗？","警告",{confirmButtonText:"确定",cancelButtonText:"取消",type:"warning"}).then(async()=>{try{await e.deleteTask(i.id),c.success("删除成功"),await e.fetchTasks()}catch{c.error("删除失败")}})};return(i,a)=>{const p=r("el-button"),m=r("el-table-column"),R=r("el-tag"),S=r("el-button-group"),D=r("el-table"),T=r("el-input"),y=r("el-form-item"),v=r("el-option"),G=r("el-select"),H=r("el-form"),M=r("el-dialog"),P=q("loading");return V(),j("div",Q,[b("div",W,[t(p,{type:"primary",onClick:x},{default:o(()=>a[6]||(a[6]=[u(" 创建任务 ")])),_:1})]),F((V(),J(D,{data:h(_),style:{width:"100%"}},{default:o(()=>[t(m,{prop:"id",label:"ID",width:"80"}),t(m,{prop:"name",label:"任务名称"}),t(m,{prop:"type",label:"任务类型",width:"120"}),t(m,{prop:"cron",label:"Cron表达式",width:"150"}),t(m,{prop:"status",label:"状态",width:"100"},{default:o(({row:l})=>[t(R,{type:l.status==="RUNNING"?"success":"info"},{default:o(()=>[u(N(l.status==="RUNNING"?"运行中":"已停止"),1)]),_:2},1032,["type"])]),_:1}),t(m,{label:"操作",width:"200"},{default:o(({row:l})=>[t(S,null,{default:o(()=>[t(p,{type:l.status==="RUNNING"?"warning":"success",size:"small",onClick:g=>B(l)},{default:o(()=>[u(N(l.status==="RUNNING"?"停止":"启动"),1)]),_:2},1032,["type","onClick"]),t(p,{type:"primary",size:"small",onClick:g=>U(l)},{default:o(()=>a[7]||(a[7]=[u(" 编辑 ")])),_:2},1032,["onClick"]),t(p,{type:"danger",size:"small",onClick:g=>E(l)},{default:o(()=>a[8]||(a[8]=[u(" 删除 ")])),_:2},1032,["onClick"])]),_:2},1024)]),_:1})]),_:1},8,["data"])),[[P,h(C)]]),t(M,{modelValue:d.value,"onUpdate:modelValue":a[5]||(a[5]=l=>d.value=l),title:k.value==="create"?"创建任务":"编辑任务",width:"500px"},{footer:o(()=>[b("span",X,[t(p,{onClick:a[4]||(a[4]=l=>d.value=!1)},{default:o(()=>a[9]||(a[9]=[u("取消")])),_:1}),t(p,{type:"primary",onClick:I},{default:o(()=>a[10]||(a[10]=[u(" 确定 ")])),_:1})])]),default:o(()=>[t(H,{model:s.value,"label-width":"100px"},{default:o(()=>[t(y,{label:"任务名称"},{default:o(()=>[t(T,{modelValue:s.value.name,"onUpdate:modelValue":a[0]||(a[0]=l=>s.value.name=l)},null,8,["modelValue"])]),_:1}),t(y,{label:"任务类型"},{default:o(()=>[t(G,{modelValue:s.value.type,"onUpdate:modelValue":a[1]||(a[1]=l=>s.value.type=l)},{default:o(()=>[t(v,{label:"HTTP",value:"HTTP"}),t(v,{label:"Shell",value:"SHELL"}),t(v,{label:"Email",value:"EMAIL"})]),_:1},8,["modelValue"])]),_:1}),t(y,{label:"Cron表达式"},{default:o(()=>[t(T,{modelValue:s.value.cron,"onUpdate:modelValue":a[2]||(a[2]=l=>s.value.cron=l)},null,8,["modelValue"])]),_:1}),t(y,{label:"任务配置"},{default:o(()=>[t(T,{modelValue:s.value.config,"onUpdate:modelValue":a[3]||(a[3]=l=>s.value.config=l),type:"textarea",rows:4},null,8,["modelValue"])]),_:1})]),_:1},8,["model"])]),_:1},8,["modelValue","title"])])}}},te=z(Y,[["__scopeId","data-v-09a0059f"]]);export{te as default};
