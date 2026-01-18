<template>
  <div class="global-footer">
    <div class="footer-content">
      <p class="copyright">
        AI项目 by
        <a href="" target="_blank" rel="noopener noreferrer" class="author-link"> 阿泽 </a>
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
// 组件逻辑（如果需要的话）
</script>

<style scoped>
.global-footer {
  width: 100%;
  background: #f5f5f5;
  border-top: 1px solid #e8e8e8;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 16px 24px;
  text-align: center;
}

.copyright {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.author-link {
  color: #1890ff;
  text-decoration: none;
  font-weight: 500;
}

.author-link:hover {
  color: #40a9ff;
  text-decoration: underline;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .footer-content {
    padding: 12px 16px;
  }

  .copyright {
    font-size: 13px;
  }
}
</style>

