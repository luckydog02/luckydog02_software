<!--
 * 数据统计图
 *
-->
<template>
  <div class="Echarts">
    <div class="chart-container">
      <el-card class="chart-card" shadow="hover">
        <div id="orderSection" class="chart-item"></div>
      </el-card>
    </div>
    <div class="chart-row">
      <el-card class="chart-card" shadow="hover">
        <div id="orderGender" class="chart-item"></div>
      </el-card>
      <el-card class="chart-card" shadow="hover">
        <div id="patientAge" class="chart-item"></div>
      </el-card>
    </div>
  </div>
</template>
<script>
import request from "@/utils/request.js";
export default {
  name: "dataExpore",
  data() {
    return {
      sevenDate: [],
      sevenOrder: [],
    };
  },
  methods: {
    //统计患者年龄分布
    patientAge(){
      var myChart = this.$echarts.init(document.getElementById("patientAge"));
      request.get("patient/patientAge")
      .then(res => {
        // 低饱和度的柔和多色配色方案
        const colorPalette = [
          '#7db3d5', // 柔和蓝
          '#8bc99a', // 柔和绿
          '#b19cd9', // 柔和紫
          '#e8a5c2', // 柔和粉
          '#f5a97f', // 柔和橙
          '#7dd3c0', // 柔和青
          '#a8c8e0', // 浅蓝
          '#b3d9b8', // 浅绿
          '#c8b3e0', // 浅紫
          '#f0b8d0'  // 浅粉
        ];
        
        var option = {
          title: {
            text: '患者年龄段分布',
            left: 'center',
            textStyle: {
              fontSize: 18,
              fontWeight: 600,
              color: '#303133'
            }
          },
          tooltip: {
            trigger: 'item',
            backgroundColor: 'rgba(255, 255, 255, 0.95)',
            borderColor: '#d3dce6',
            borderWidth: 1,
            textStyle: {
              color: '#303133'
            }
          },
          legend: {
            top: '8%',
            left: 'center',
            textStyle: {
              color: '#606266'
            }
          },
          color: colorPalette,
          series: [
            {
              name: '年龄段',
              type: 'pie',
              radius: ['40%', '70%'],
              avoidLabelOverlap: false,
              itemStyle: {
                borderRadius: 10,
                borderColor: '#fff',
                borderWidth: 2
              },
              label: {
                show: false,
                position: 'center'
              },
              emphasis: {
                label: {
                  show: true,
                  fontSize: '40',
                  fontWeight: 'bold',
                  color: '#606266'
                },
                itemStyle: {
                  shadowBlur: 20,
                  shadowOffsetX: 0,
                  shadowColor: 'rgba(0, 0, 0, 0.15)'
                }
              },
              labelLine: {
                show: false
              },
              data: [
                {value: res.data.data[0], name: '0-9岁'},
                {value: res.data.data[1], name: '10-19岁'},
                {value: res.data.data[2], name: '20-29岁'},
                {value: res.data.data[3], name: '30-39岁'},
                {value: res.data.data[4], name: '40-49岁'},
                {value: res.data.data[5], name: '50-59岁'},
                {value: res.data.data[6], name: '60-69岁'},
                {value: res.data.data[7], name: '70-79岁'},
                {value: res.data.data[8], name: '80-89岁'},
                {value: res.data.data[9], name: '90-99岁'},
              ]
            }
          ]
        };
        myChart.setOption(option);
      }).catch(err => {
        console.error(err);
      })
    },

    //统计挂号科室人数
    orderSection(){
      let myChart = this.$echarts.init(document.getElementById("orderSection"));
      request.get("order/orderSection")
      .then(res => {
        var option = {
          title: {
            text: '挂号科室人数统计',
            left: 'center',
            textStyle: {
              fontSize: 18,
              fontWeight: 600,
              color: '#303133'
            }
          },
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              type: 'shadow'
            },
            backgroundColor: 'rgba(255, 255, 255, 0.95)',
            borderColor: '#d3dce6',
            borderWidth: 1,
            textStyle: {
              color: '#303133'
            }
          },
          grid: {
            left: '3%',
            right: '4%',
            bottom: '15%',
            containLabel: true
          },
          xAxis: {
            type: 'category',
            data: res.data.data.map((item) => item.doctor.dSection),
            axisLabel: {
              interval: 0,
              rotate: 10,
              color: '#606266',
              fontSize: 12
            },
            axisLine: {
              lineStyle: {
                color: '#e4e7ed'
              }
            }
          },
          yAxis: {
            type: 'value',
            axisLabel: {
              color: '#606266'
            },
            axisLine: {
              lineStyle: {
                color: '#e4e7ed'
              }
            },
            splitLine: {
              lineStyle: {
                color: '#f0f2f5',
                type: 'dashed'
              }
            }
          },
          series: [{
            data: res.data.data.map((item, index) => {
              // 为每个柱子设置不同的低饱和度颜色
              const colors = [
                ['#7db3d5', '#a8c8e0'], // 柔和蓝
                ['#8bc99a', '#b3d9b8'], // 柔和绿
                ['#b19cd9', '#c8b3e0'], // 柔和紫
                ['#e8a5c2', '#f0b8d0'], // 柔和粉
                ['#f5a97f', '#f8c4a8'], // 柔和橙
                ['#7dd3c0', '#a8e0d3'], // 柔和青
                ['#a8c8e0', '#c8d8e8'], // 浅蓝
                ['#b3d9b8', '#d0e6d5'], // 浅绿
                ['#c8b3e0', '#ddd0e8'], // 浅紫
                ['#f0b8d0', '#f5d0e0']  // 浅粉
              ];
              const colorPair = colors[index % colors.length];
              return {
                value: item.countSection,
                itemStyle: {
                  color: new this.$echarts.graphic.LinearGradient(0, 0, 0, 1, [
                    {offset: 0, color: colorPair[0]},
                    {offset: 1, color: colorPair[1]}
                  ]),
                  borderRadius: [4, 4, 0, 0]
                }
              };
            }),
            type: 'bar',
            showBackground: true,
            backgroundStyle: {
              color: 'rgba(0, 0, 0, 0.02)'
            },
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowColor: 'rgba(0, 0, 0, 0.15)'
              }
            }
          }]
        };
        myChart.setOption(option);
      })
      .catch(err => {
        console.error(err);
      })
    },

    //挂号男女比例
    orderGender(){
      var myChart = this.$echarts.init(document.getElementById("orderGender"));
      request.get("order/orderGender",)
      .then(res => {
        // 修复硬编码索引问题，使用数据驱动方式
        const genderData = res.data.data || [];
        const chartData = genderData.map(item => ({
          value: item.countGender || 0,
          name: item.patient?.pGender || '未知'
        }));
        
        // 如果没有数据，显示提示
        if (chartData.length === 0) {
          chartData.push({value: 0, name: '暂无数据'});
        }
        
        // 根据性别设置低饱和度柔和颜色
        const genderColors = {
          '男': '#7db3d5',      // 柔和蓝
          '女': '#e8a5c2',      // 柔和粉
          '未知': '#c0c4cc'     // 柔和灰
        };
        
        // 为每个数据项设置颜色
        chartData.forEach(item => {
          item.itemStyle = {
            color: genderColors[item.name] || '#409EFF'
          };
        });
        
        var option = {
          title: {
            text: '患者性别比例',
            left: 'center',
            textStyle: {
              fontSize: 18,
              fontWeight: 600,
              color: '#303133'
            }
          },
          tooltip: {
            trigger: 'item',
            backgroundColor: 'rgba(255, 255, 255, 0.95)',
            borderColor: '#d3dce6',
            borderWidth: 1,
            textStyle: {
              color: '#303133'
            }
          },
          legend: {
            orient: 'vertical',
            left: 'left',
            textStyle: {
              color: '#606266'
            }
          },
          series: [
            {
              name: '人数',
              type: 'pie',
              radius: ['40%', '70%'],
              avoidLabelOverlap: false,
              itemStyle: {
                borderRadius: 10,
                borderColor: '#fff',
                borderWidth: 2
              },
              label: {
                show: true,
                formatter: '{b}: {c}\n({d}%)',
                color: '#606266'
              },
              emphasis: {
                label: {
                  show: true,
                  fontSize: 16,
                  fontWeight: 'bold',
                  color: '#303133'
                },
                itemStyle: {
                  shadowBlur: 20,
                  shadowOffsetX: 0,
                  shadowColor: 'rgba(0, 0, 0, 0.15)'
                }
              },
              data: chartData
            }
          ]
        };
        myChart.setOption(option);

      })
      .catch(err => {
        console.error(err);
      });
    },
    //获取过去num天日期
    pastSeven(num) {
      let date = new Date();
      date.setDate(date.getDate() - num);
      let time = date.getMonth() + 1 + "-" + date.getDate();
      return time;
    },

  },
  mounted() {
    this.orderGender();
    this.orderSection();
    this.patientAge();
  },
  created() {

  },
};
</script>

<style scoped lang="scss">
.Echarts {
    padding: 20px;
    background: #f5f7fa;
    min-height: calc(100vh - 70px);
}

.chart-container {
    margin-bottom: 30px;
    
    .chart-card {
        border-radius: 16px;
        border: 1px solid #e4e7ed;
        transition: all 0.3s ease;
        
        &:hover {
            box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
            transform: translateY(-2px);
        }
        
        ::v-deep .el-card__body {
            padding: 20px;
        }
        
        .chart-item {
            width: 100%;
            height: 400px;
        }
    }
}

.chart-row {
    display: flex;
    gap: 30px;
    justify-content: space-between;
    
    .chart-card {
        flex: 1;
        border-radius: 16px;
        border: 1px solid #e4e7ed;
        transition: all 0.3s ease;
        
        &:hover {
            box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
            transform: translateY(-2px);
        }
        
        ::v-deep .el-card__body {
            padding: 20px;
        }
        
        .chart-item {
            width: 100%;
            height: 500px;
        }
    }
}

// 响应式设计
@media (max-width: 1200px) {
    .chart-row {
        flex-direction: column;
        
        .chart-card .chart-item {
            height: 400px;
        }
    }
    
    .chart-container .chart-card .chart-item {
        height: 350px;
    }
}
</style>
