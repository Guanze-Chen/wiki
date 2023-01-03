<template>
    <div>
      <a-row>
        <a-col :span="24">
          <a-card>
            <a-row>
              <a-col :span="8">
                <a-statistic title="总阅读量" :value="stat.viewCount">
                  <template #suffix>
                    <user-outlined />
                  </template>
                </a-statistic>
              </a-col>
              <a-col :span="8">
                <a-statistic title="总点赞量" :value="stat.voteCount">
                  <template #suffix>
                    <like-outlined />
                  </template>
                </a-statistic>
              </a-col>
              <a-col :span="8">
                <a-statistic title="点赞率" :value="stat.voteCount / stat.viewCount * 100"
                             :precision="2"
                             suffix="%"
                             :value-style="{ color: '#cf1322' }">
                  <template #suffix>
                    <like-outlined />
                  </template>
                </a-statistic>
              </a-col>
            </a-row>
          </a-card>
        </a-col>
      </a-row>
      <br />
      <a-row :gutter="16">
        <a-col :span="12">
          <a-card>
            <a-row>
              <a-col :span="12">
                <a-statistic title="今日阅读" :value="stat.todayViewCount" style="margin-right: 50px">
                  <template #suffix>
                    <UserOutlined />
                  </template>
                </a-statistic>
              </a-col>
              <a-col :span="12">
                <a-statistic title="今日点赞" :value="stat.todayVoteCount">
                  <template #suffix>
                    <like-outlined />
                  </template>
                </a-statistic>
              </a-col>
            </a-row>
          </a-card>
        </a-col>
        <a-col :span="12">
          <a-card>
            <a-row>
              <a-col :span="12">
                <a-statistic
                    title="预计今日阅读"
                    :value="stat.todayViewIncrease"
                    :value-style="{ color: '#0000ff' }"
                >
                  <template #suffix>
                    <UserOutlined />
                  </template>

                </a-statistic>
              </a-col>
              <a-col :span="12">
                <a-statistic
                    title="预计今日阅读增长"
                    :value="stat.todayViewIncreaseRateAbs"
                    :precision="2"
                    suffix="%"
                    class="demo-class"
                    :value-style="stat.todayViewIncreaseRate < 0 ? { color: '#3f8600' } : { color: '#cf1322' }"
                >
                  <template #prefix>
                    <arrow-down-outlined v-show="stat.todayViewIncreaseRate < 0"/>
                    <arrow-up-outlined v-show="stat.todayViewIncreaseRate >= 0"/>
                  </template>
                </a-statistic>
              </a-col>
            </a-row>
          </a-card>
        </a-col>
      </a-row>

    </div>
</template>

<script>
import {defineComponent, ref, onMounted} from "vue";
import axios from 'axios';
import {UserOutlined, LikeOutlined, ArrowDownOutlined, ArrowUpOutlined} from '@ant-design/icons-vue';

export default defineComponent({
  name: "TheWelcome",
  components: {
    UserOutlined,
    LikeOutlined,
    ArrowDownOutlined,
    ArrowUpOutlined
  },
  setup() {
    const stat = ref();
    stat.value = {};
    const getStat = () => {
      axios.get('/ebook-snapshot/get-statistic')
          .then((res) => {
            const data = res.data;
            if (data.success) {
              const statResp = data.content;
              // console.log('-----stat-----');
              // console.log(statResp);
              // console.log(statResp.length);
              // console.log('---stat--end-')

              if(statResp.length > 1) {
                stat.value.viewCount = statResp[1].viewCount;
                stat.value.voteCount = statResp[1].voteCount;
                stat.value.totalViewCount = statResp[1].viewIncrease;
                stat.value.totalVoteCount = statResp[1].voteIncrease;

                // 按分钟计算当前时间点，占一天的百分比
                const now = new Date();
                const nowRate = (now.getHours() * 60 + now.getMinutes()) / (60 * 24);

                stat.value.todayViewIncrease = parseInt(String(statResp[1].viewIncrease / nowRate));
                // todayViewIncreaseRate：今日预计增长率
                stat.value.todayViewIncreaseRate = (stat.value.todayViewIncrease - statResp[0].viewIncrease) / statResp[0].viewIncrease * 100;
                stat.value.todayViewIncreaseRateAbs = Math.abs(stat.value.todayViewIncreaseRate);
              } else {
                stat.value.viewCount = statResp[0].viewCount;
                stat.value.voteCount = statResp[0].voteCount;
                stat.value.totalViewCount = statResp[0].viewIncrease;
                stat.value.totalVoteCount = statResp[0].voteIncrease;
                stat.value.todayViewIncrease = 0;
                stat.value.todayViewIncreaseRate = 0;
                stat.value.todayViewIncreaseRateAbs = 0;

              }
            }
          });
    };
    onMounted(() => {
      getStat();
    });

    return {
      stat,
    }
  }
})
</script>

<style scoped>

</style>