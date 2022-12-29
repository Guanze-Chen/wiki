<template>
  <a-layout-footer style="text-align: center">
    wiki created by vue3 antd {{user.name}}
  </a-layout-footer>
</template>

<script lang="ts">
import { defineComponent, computed } from 'vue';
import store from "@/store";

export default defineComponent({
  name: 'TheFooter',
  setup() {
    const user = computed( () =>
       store.state.user)
    return {
      user,
    }
  }
});
</script>