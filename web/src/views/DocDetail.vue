<template>
  <a-layout>
    <a-layout-content :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }">
      <h3 v-if="level1.length === 0">对不起，相关文档还在收集中！</h3>
      <a-row>
        <a-col :span="6">
          <a-tree
              v-if="level1&&level1.length > 0"
              :tree-data="level1"
              @select="onSelect"
              :field-names="{title: 'name', key: 'id', value: 'id'}"
              :defaultExpandAll="true"
              :defaultSelectedKeys="defaultSelectedKeys"
          >
          </a-tree>
        </a-col>
        <a-col :span="18">
          <div>
            <h2>{{doc.name}}</h2>
            <div>
              <span>阅读数: {{doc.viewCount}}</span> &nbsp;&nbsp;
              <span>点赞数: {{doc.voteCount}}</span>
            </div>
            <a-divider style="height: 2px;background-color:#9999cc" />
          </div>
          <div class="wangeditor" :innerHTML="html"></div>
        </a-col>
      </a-row>
    </a-layout-content>
  </a-layout>
</template>

<script lang="ts">
import {createVNode, defineComponent, onMounted, ref} from 'vue';
import { SearchOutlined, ExclamationCircleOutlined } from '@ant-design/icons-vue';
import { message } from "ant-design-vue";
import axios from 'axios';
import {Tool} from "@/utils/tool";
import {useRoute} from "vue-router";





export default defineComponent({
  name: 'DocDetail',
  components: {
    SearchOutlined,
    ExclamationCircleOutlined,
  },
  setup() {
    const route = useRoute();
    const docs = ref();
    const html = ref();
    const defaultSelectedKeys = ref();
    defaultSelectedKeys.value = [];

    //当前选中的文档
    const doc = ref();
    doc.value = {};

    /**
     * 一级分类树，children属性就是二级分类
     * [{
     *   id: "",
     *   name: "",
     *   children: [{
     *     id: "",
     *     name: "",
     *   }]
     * }]
     */
    const level1 = ref(); // 一级分类树，children属性就是二级分类
    level1.value = [];


    const setDisable = (treeSelectData: any, id: any) => {
      // console.log(treeSelectData, id);
      // 遍历数组，即遍历某一层节点
      for (let i = 0; i < treeSelectData.length; i++) {
        const node = treeSelectData[i];
        if (node.id === id) {
          // 如果当前节点就是目标节点
          console.log("disabled", node);
          // 将目标节点设置为disabled
          node.disabled = true;

          // 遍历所有子节点，将所有子节点全部都加上disabled
          const children = node.children;
          if (Tool.isNotEmpty(children)) {
            for (let j = 0; j < children.length; j++) {
              setDisable(children, children[j].id)
            }
          }
        } else {
          // 如果当前节点不是目标节点，则到其子节点再找找看。
          const children = node.children;
          if (Tool.isNotEmpty(children)) {
            setDisable(children, id);
          }
        }
      }
    };


    const handleQueryContent = (id:number) => {

      axios.get('/doc/mediumtext/' + id, {
      })
          .then((res) => {
            const data = res.data;
            if (data.success) {
              html.value = data.content;

            } else {
              message.error(data.message);
            }
          })
    };

    const handleQuery = () => {

      axios.get('/doc/all/' + route.query.ebookId, {
      })
          .then((res) => {
            const data = res.data;
            if (data.success) {
              docs.value = data.content;

              level1.value = [];
              level1.value = Tool.array2Tree(docs.value, 0);

              if (Tool.isNotEmpty(level1)) {
                defaultSelectedKeys.value = [level1.value[0].id]; // 选中状态
                handleQueryContent(level1.value[0].id); // 进行查询
                // 初始显示文档信息
                doc.value = level1.value[0];
              }
            } else {
              message.error(data.message);
            }
          })
    };



    const onSelect = (selectedKeys: any, info: any) => {
      console.log('selected', selectedKeys, info);
      if(Tool.isNotEmpty(selectedKeys)) {
        // 选中某一个节点时候，加载该文档的节点信息
        doc.value = info.selectedNodes[0].props;
        //加载内容
        handleQueryContent(selectedKeys[0]);

      }
    }




    onMounted(() => {
      handleQuery();
    })

    return {

     level1,

      html,
      onSelect,

      defaultSelectedKeys,

      doc,
    }




  }
});
</script>

<style>
/* wangeditor默认样式, 参照: http://www.wangeditor.com/doc/pages/02-%E5%86%85%E5%AE%B9%E5%A4%84%E7%90%86/03-%E8%8E%B7%E5%8F%96html.html */
/* table 样式 */
.wangeditor table {
  border-top: 1px solid #ccc;
  border-left: 1px solid #ccc;
}
.wangeditor table td,
.wangeditor table th {
  border-bottom: 1px solid #ccc;
  border-right: 1px solid #ccc;
  padding: 3px 5px;
}
.wangeditor table th {
  border-bottom: 2px solid #ccc;
  text-align: center;
}

/* blockquote 样式 */
.wangeditor blockquote {
  display: block;
  border-left: 8px solid #d0e5f2;
  padding: 5px 10px;
  margin: 10px 0;
  line-height: 1.4;
  font-size: 100%;
  background-color: #f1f1f1;
}

/* code 样式 */
.wangeditor code {
  display: inline-block;
  *display: inline;
  *zoom: 1;
  background-color: #f1f1f1;
  border-radius: 3px;
  padding: 3px 5px;
  margin: 0 3px;
}
.wangeditor pre code {
  display: block;
}

/* ul ol 样式 */
.wangeditor ul, ol {
  margin: 10px 0 10px 20px;
}

/* 和antdv p冲突，覆盖掉 */
.wangeditor blockquote p {
  font-family:"YouYuan";
  margin: 20px 10px !important;
  font-size: 16px !important;
  font-weight:600;
}

</style>