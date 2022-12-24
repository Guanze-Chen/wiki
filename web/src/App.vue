<template>
  <a-layout>
    <TheHeader/>
    <router-view />
    <TheFooter/>
  </a-layout>
</template>

<script lang="ts">
  import {defineComponent} from "vue";
  import TheHeader from "@/components/Header.vue";
  import TheFooter from "@/components/TheFooter.vue";
  export default defineComponent({
    name:'App',
    components: {
      TheHeader,
      TheFooter,
    }

  })

</script>

<style>

</style>
