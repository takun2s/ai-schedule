<template>
  <el-form :model="form" :rules="rules" ref="form" label-width="120px">
    <el-form-item label="任务名称" prop="name">
      <el-input v-model="form.name"></el-input>
    </el-form-item>
    <el-form-item label="任务类型" prop="type">
      <el-select v-model="form.type">
        <el-option label="命令行" value="COMMAND"></el-option>
        <el-option label="HTTP" value="HTTP"></el-option>
        <el-option label="Python" value="PYTHON"></el-option>
      </el-select>
    </el-form-item>
    <el-form-item label="命令内容" prop="command">
      <el-input type="textarea" v-model="form.command"></el-input>
    </el-form-item>
  </el-form>
</template>
