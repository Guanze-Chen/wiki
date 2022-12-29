<template>
  <a-layout-header class="header">
    <div class="logo" />
    <a class="login-menu" v-show="user.id">
      <span>欢迎, {{user.name}}</span>
    </a>
    <a class="login-menu" v-show="!user.id" @click="showLoginModal">
      <span>登录</span>
    </a>
    <a-menu
        theme="dark"
        mode="horizontal"
        :style="{ lineHeight: '64px' }"
    >
      <a-menu-item key="1">
        <router-link to="/">首页</router-link>
      </a-menu-item>
      <a-menu-item key="2">
        <router-link to="/ebook">电子书管理</router-link>
      </a-menu-item>
      <a-menu-item key="3">
        <router-link to="/category">分类管理</router-link>
      </a-menu-item>
      <a-menu-item key="2">
        <router-link to="/user">用户管理</router-link>
      </a-menu-item>
      <a-menu-item key="4">
        <router-link to="/about">关于本站</router-link>
      </a-menu-item>
    </a-menu>

    <a-modal
      title="登录"
      v-model:visible="loginModalVisible"
      :confirm-loading="loginModalLoading"
      @ok="login"
    >
      <a-form
      :model="loginUser"
      :label-col="{ span: 6 }"
      :wrapper-col="{ span: 18 }"
      >
        <a-form-item label="登录名">
          <a-input v-model:value="loginUser.loginName"/>
        </a-form-item>

        <a-form-item label="密码">
          <a-input-password v-model:value="loginUser.password"/>
        </a-form-item>
      </a-form>

    </a-modal>

  </a-layout-header>
</template>

<script lang="ts">
import { defineComponent,ref } from 'vue';
import axios from 'axios';
import {message} from "ant-design-vue";
import store from "@/store";

declare let hexMd5: any;
declare let KEY: any;

export default defineComponent({
  name: 'TheHeader',
  setup() {

    // 登录的user
    const loginUser = ref({
      loginName: "test",
      password:"123456"
    });

    // 显示的user

    const user = ref();
    user.value = {};

    const loginModalVisible = ref(false);
    const loginModalLoading = ref(false);
    const showLoginModal = () => {
      loginModalVisible.value = true;
    };

    const login = () => {
      console.log("开始登录");
      loginModalLoading.value = true;
      loginUser.value.password = hexMd5(loginUser.value.password + KEY);
      axios.post('/user/login', loginUser.value)
          .then((res) => {
            loginModalLoading.value = false;
            const data = res.data;
            if (data.success) {
              loginModalLoading.value = false;
              message.success("登录成功!");
              loginModalVisible.value = false;
              user.value = data.content;
              store.commit("setUser", user.value);
            } else {
              message.error(data.message);
              loginModalVisible.value = false;
            }
          })
    };

    return {
      loginModalVisible,
      loginModalLoading,
      showLoginModal,
      login,
      loginUser,
      user,
    }

  }
});
</script>

<style>
  .login-menu {
    float: right;
    color: white;
  }
</style>