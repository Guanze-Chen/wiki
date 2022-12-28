<template>
  <a-layout>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <a-row :gutter="24">
        <a-col :span="8">
          <div>
            <a-form
                layout="inline"
                :model="param"
            >
              <a-form-item
              >
                <a-input v-model:value="param.name" placeholder="输入名称">
                  <template #prefix>
                    <search-outlined class="site-form-item-icon" />
                  </template>
                </a-input>
              </a-form-item>

              <a-form-item>
                <a-button type="primary" @click="handleQuery()">
                  查询
                </a-button>
              </a-form-item>

              <a-form-item>
                <a-button type="primary" @click="add">
                  新增
                </a-button>
              </a-form-item>

            </a-form>
          </div>
          <a-table
              v-if="level1&&level1.length > 0"
              :columns="columns"
              :data-source="level1"
              :pagination=false
              size="small"
              :defaultExpandAllRows="true"
          >
            <template #bodyCell="{column, record}">
              <template v-if="column.key === 'name'">
                {{record.sort}} {{record.name}}
              </template>

              <template v-else-if="column.key === 'Action'">
                <a-space size="small">
                  <a-button type="primary" size="small" @click="edit(record)">
                    编辑
                  </a-button>

                  <a-popconfirm
                      title="Are you sure delete this record?"
                      ok-text="Yes"
                      cancel-text="No"
                      @confirm="handleDelete(record.id)"
                  >
                    <a-button size="small" type="danger" >
                      删除
                    </a-button>
                  </a-popconfirm>

                </a-space>
              </template>

            </template>

          </a-table>
        </a-col>
        <a-col :span="16">
          <p>
            <a-form layout="inline" :model="param">
              <a-form-item>
                <a-button type="primary" @click="handleSave">
                  保存
                </a-button>
              </a-form-item>
            </a-form>
          </p>
          <a-form
              :model="doc"
              layout="vertical"
          >
            <a-form-item
                label="父文档"
            >
              <a-tree-select
                  v-model:value="doc.parent"
                  show-search
                  style="width: 100%"
                  :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
                  placeholder="Please select"
                  allow-clear
                  tree-default-expand-all
                  :tree-data="treeSelectData"
                  :field-names="{
              title: 'name',
              label: 'name',
              value: 'id',
            }"
              >

              </a-tree-select>

            </a-form-item>

            <a-form-item
                label="名称"
            >
              <a-input v-model:value="doc.name" />
            </a-form-item>

            <a-form-item
                label="序号"
            >
              <a-input v-model:value="doc.sort" />
            </a-form-item>

            <a-form-item>
              <a-button type="primary" @click="handlePreviewContent">
                <EyeOutlined />
                内容预览
              </a-button>
            </a-form-item>

            <a-form-item label="内容">
              <div id="content">
                <Toolbar
                    style="border-bottom: 1px solid #ccc"
                    :editor="editorRef"
                    :defaultConfig="toolbarConfig"
                    :mode="mode"
                />
                <Editor
                    style="height: 500px; overflow-y: hidden;"
                    v-model="valueHtml"
                    :defaultConfig="editorConfig"
                    :mode="mode"
                    @onCreated="handleCreated"
                />
              </div>
            </a-form-item>

          </a-form>
        </a-col>
      </a-row>

      <a-drawer width="900" placement="right" :closable="false" :visible="drawerVisible" @close="onDrawerClose">
        <div class="wangeditor" :innerHTML="previewHtml"></div>
      </a-drawer>
    </a-layout-content>
  </a-layout>
<!--  <a-modal-->
<!--      v-model:visible="modalVisible"-->
<!--      title="编辑文档"-->
<!--      :confirm-loading="modalLoading"-->
<!--      @ok="handleOk"-->
<!--  >-->
<!--    <a-form-->
<!--        :model="doc"-->
<!--        :label-col="{ span: 8 }"-->
<!--        :wrapper-col="{ span: 16 }"-->
<!--    >-->
<!--      <a-form-item-->
<!--          label="父文档"-->
<!--      >-->
<!--        <a-tree-select-->
<!--            v-model:value="doc.parent"-->
<!--            show-search-->
<!--            style="width: 100%"-->
<!--            :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"-->
<!--            placeholder="Please select"-->
<!--            allow-clear-->
<!--            tree-default-expand-all-->
<!--            :tree-data="treeSelectData"-->
<!--            :field-names="{-->
<!--              title: 'name',-->
<!--              label: 'name',-->
<!--              value: 'id',-->
<!--            }"-->
<!--        >-->

<!--        </a-tree-select>-->

<!--      </a-form-item>-->

<!--      <a-form-item-->
<!--          label="名称"-->
<!--      >-->
<!--        <a-input v-model:value="doc.name" />-->
<!--      </a-form-item>-->

<!--      <a-form-item-->
<!--          label="序号"-->
<!--      >-->
<!--        <a-input v-model:value="doc.sort" />-->
<!--      </a-form-item>-->

<!--      <a-form-item label="内容">-->
<!--        <div id="content">-->
<!--          <Toolbar-->
<!--              style="border-bottom: 1px solid #ccc"-->
<!--              :editor="editorRef"-->
<!--              :defaultConfig="toolbarConfig"-->
<!--              :mode="mode"-->
<!--          />-->
<!--          <Editor-->
<!--              style="height: 500px; overflow-y: hidden;"-->
<!--              v-model="valueHtml"-->
<!--              :defaultConfig="editorConfig"-->
<!--              :mode="mode"-->
<!--              @onCreated="handleCreated"-->
<!--          />-->
<!--        </div>-->
<!--      </a-form-item>-->

<!--    </a-form>-->
<!--  </a-modal>-->
</template>

<script lang="ts">
import {createVNode, defineComponent, shallowRef, onMounted, ref} from 'vue';
import { SearchOutlined, ExclamationCircleOutlined, EyeOutlined } from '@ant-design/icons-vue';
import { message, Modal } from "ant-design-vue";
import axios from 'axios';
import {Tool} from "@/utils/tool";
import {useRoute} from "vue-router";
import '@wangeditor/editor/dist/css/style.css' // 引入 css
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'





export default defineComponent({
  name: 'Doc',
  components: {
    SearchOutlined,
    ExclamationCircleOutlined,
    Editor,
    Toolbar,
    EyeOutlined,
  },
  setup() {
    const route = useRoute();

    const docs = ref();
    const param = ref();
    param.value = {};
    const loading = ref(false);


    // 编辑器
    // 编辑器实例，必须用 shallowRef
    const editorRef = shallowRef();

    // 内容 HTML
    const valueHtml = ref('');

    const toolbarConfig = {};
    const editorConfig = { placeholder: '请输入内容...' };

    const handleCreated = (editor: any) => {
      editorRef.value = editor // 记录 editor 实例，重要！
    }


    const columns = [
      // {
      //   title:'一级分类',
      //   dataIndex:'parent',
      //   key: 'parent'
      // },
      {
        title:'名称',
        dataIndex:'name',
        key: 'name'
      },
      {
        title:'序号',
        dataIndex:'sort',
        key: 'sort'
      },
      {
        title:'操作',
        dataIndex:'Action',
        key: 'Action'
      },

    ]

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
    let treeSelectData = ref();
    treeSelectData.value = [];

    const deleteIds: Array<string> = [];
    const deleteNames: Array<string> = [];




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

    // 查找整课数的id
    const getDeleteIds = (treeSelectData: any, id: any) => {
      // console.log(treeSelectData, id);
      // 遍历数组，即遍历某一层节点
      for (let i = 0; i < treeSelectData.length; i++) {
        const node = treeSelectData[i];
        if (node.id === id) {
          // 如果当前节点就是目标节点
          // 将目标节点添加进入ids
          deleteIds.push(id);
          deleteNames.push(node.name);

          const children = node.children;
          if (Tool.isNotEmpty(children)) {
            for (let j = 0; j < children.length; j++) {
              getDeleteIds(children, children[j].id)
            }
          }
        } else {
          // 如果当前节点不是目标节点，则到其子节点再找找看。
          const children = node.children;
          if (Tool.isNotEmpty(children)) {
            getDeleteIds(children, id);
          }
        }
      }
    };

    const handleQuery = () => {
      loading.value = true;
      // 清空现有数据
      level1.value = [];
      axios.get('/doc/all/' + route.query.ebookId, {
      })
          .then((res) => {
            loading.value = false;
            const data = res.data;
            if (data.success) {
              docs.value = data.content;

              level1.value = [];
              level1.value = Tool.array2Tree(docs.value, 0);

              treeSelectData.value = Tool.copy(level1.value);
              treeSelectData.value.unshift({id: 0, name: '无'});
            } else {
              message.error(data.message);
            }
          })
    };


    const modalVisible = ref(false);
    const modalLoading = ref(false);



    const doc = ref();
    doc.value = {};


    // 内容查询

    const handleQueryContent = () => {
      axios.get('/doc/mediumtext/' + doc.value.id, {
      })
          .then((res) => {
            const data = res.data;
            if (data.success) {
              valueHtml.value = data.content
            } else {
              message.error(data.message);
            }
          })
    };


    const edit = (record: any) => {
      // 清空富文本框
      valueHtml.value = "";
      modalVisible.value = true;
      doc.value = Tool.copy(record);

      handleQueryContent();

      //不能选择当前节点及其所有子孙节点作为父节点
      treeSelectData.value = Tool.copy(level1.value);
      setDisable(treeSelectData.value, record.id);

      // 为选择数添加一个无
      treeSelectData.value.unshift({id:0, name: '无'});
    }

    const add = () => {
      valueHtml.value = "";
      message.success("请在右边编辑器添加!")
      modalVisible.value = true;
      doc.value = {
        ebookId: route.query.ebookId
      };

      treeSelectData.value = Tool.copy(level1.value);
      treeSelectData.value.unshift({id:0, name: '无'});
    }

    const handleDelete = (id: number) => {
      // 先清除之前存储过的数据
      deleteIds.length = 0;
      deleteNames.length = 0;
      getDeleteIds(level1.value, id);
      Modal.confirm({
        title: '重要提醒',
        icon: createVNode(ExclamationCircleOutlined),
        content: '将删除：【' + deleteNames.join("，") + "】删除后不可恢复，确认删除？",
        onOk() {
          axios.delete("/doc/delete" + deleteIds.join(","))
              .then((res) => {
                const data = res.data;
                if (data.success) {
                  message.success("删除成功");
                  handleQuery();
                }
              })
        }

      })

    }

    const handleSave = () => {
      modalLoading.value = true;
      doc.value.content = valueHtml.value;
      axios.post("/doc/save", doc.value)
          .then((res) => {
            const data = res.data;
            if (data.success) {
              // 重新加载列表
              handleQuery()
              message.success("保存成功!");
            } else {
              message.error(data.message);
            }


          })
    }

    // 富文本预览
    const drawerVisible = ref(false);
    const previewHtml = ref();
    const handlePreviewContent = () => {
      const html = valueHtml.value;
      previewHtml.value = html;
      drawerVisible.value = true;
    };

    const onDrawerClose = () => {
      drawerVisible.value = false;
    }




    onMounted(() => {
      handleQuery();
    })

    return {
      //docs,
      level1,
      columns,
      loading,
      handleSave,
      modalVisible,
      modalLoading,
      edit,
      add,
      handleDelete,
      doc,
      handleQuery,
      param,

      treeSelectData,

      editorRef,
      valueHtml,
      mode: 'default', // 或 'simple'
      toolbarConfig,
      editorConfig,
      handleCreated,

      drawerVisible,
      previewHtml,
      handlePreviewContent,
      onDrawerClose,
    }




  }
});
</script>

<style scoped>
img {
  width: 50px;
  height: 50px;
}

</style>
