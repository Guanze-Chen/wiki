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
      <br />
      <a-row>
        <a-col :span="24">
          <div id="main" style="width: 100%; height: 300px;"></div>
        </a-col>
      </a-row>

    </div>
</template>

<script lang="ts">
import {defineComponent, ref, onMounted} from "vue";
import axios from 'axios';
import {UserOutlined, LikeOutlined, ArrowDownOutlined, ArrowUpOutlined} from '@ant-design/icons-vue';

declare let echarts: any;

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

    const init30DayEcharts = (list: any) => {
      // 基于准备好的dom，初始化echarts实例
      const myChart = echarts.init(document.getElementById('main'));

      const xAxis = [];
      const seriesView = [];
      const seriesVote = [];
      for (let i = 0; i < list.length; i++) {
        const record = list[i];
        xAxis.push(record.date);
        seriesView.push(record.viewIncrease);
        seriesVote.push(record.voteIncrease);
      }

      // 指定图表的配置项和数据
      const option = {
        title: {
          text: '30天趋势图'
        },
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['总阅读量', '总点赞量']
        },
        grid: {
          left: '1%',
          right: '3%',
          bottom: '3%',
          containLabel: true
        },
        toolbox: {
          feature: {
            saveAsImage: {}
          }
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: xAxis
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '总阅读量',
            type: 'line',
            // stack: '总量', 不堆叠
            data: seriesView,
            smooth: true
          },
          {
            name: '总点赞量',
            type: 'line',
            // stack: '总量', 不堆叠
            data: seriesVote,
            smooth: true
          }
        ]
      };

      // 使用刚指定的配置项和数据显示图表。
      myChart.setOption(option);
    };

    const get30DayStatistic = () => {
      axios.get('/ebook-snapshot/get-30-statistic').then((response) => {
        const data = response.data;
        if (data.success) {
          const statisticList = data.content;

          init30DayEcharts(statisticList)
        }
      });
    };


    onMounted(() => {
      getStat();
      get30DayStatistic();
    });

    return {
      stat,
    }
  }
})
</script>

<style scoped>

</style>