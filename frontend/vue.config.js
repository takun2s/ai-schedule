module.exports = {
  devServer: {
    port: 8081,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/ws': {
        target: 'ws://localhost:8080',
        ws: true
      }
    }
  },
  productionSourceMap: false,
  outputDir: 'dist',
  assetsDir: 'static',
  css: {
    loaderOptions: {
      sass: {
        additionalData: `@import "@/styles/variables.scss";`  // 只使用 additionalData
      }
    }
  },
  transpileDependencies: [
    '@antv/g6',
    '@antv/hierarchy',
    '@antv/layout',
    '@antv/matrix-util',
    '@antv/util',
    '@antv/graphlib',
    'ml-matrix'
  ],
  chainWebpack: config => {
    config.module
      .rule('js')
      .test(/\.js$/)
      .include
        .add(/node_modules\/@antv/)
        .add(/node_modules\/ml-matrix/)
        .end()
      .use('babel-loader')
        .loader('babel-loader')
        .end();
  }
};
