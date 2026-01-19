<template>
  <div class="markdown-message" v-html="renderedContent"></div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'

interface Props {
  content: string
}

const props = defineProps<Props>()

// 配置 markdown-it
const md = new MarkdownIt({
  html: true, // 启用 HTML
  linkify: true, // 自动转换链接
  typographer: true, // 启用排版改进
  highlight: function (str, lang) {
    try {
      let result
      if (lang && hljs.getLanguage(lang)) {
        result = hljs.highlight(str, { language: lang })
      } else {
        result = hljs.highlightAuto(str)
      }
      return `<pre class="hljs"><code class="hljs language-${result.language || ''}">${result.value}</code></pre>`
    } catch (err) {
      console.warn('代码高亮失败:', err)
      return `<pre class="hljs"><code class="hljs">${md.utils.escapeHtml(str)}</code></pre>`
    }
  }
})

// 渲染内容
const renderedContent = computed(() => {
  if (!props.content) return ''
  return md.render(props.content)
})
</script>

<style scoped>
.markdown-message {
  /* 基础样式将在全局样式中定义 */
}

/* 自定义代码块样式 */
:deep(.hljs) {
  background: #f6f8fa;
  border-radius: 6px;
  padding: 16px;
  margin: 8px 0;
  overflow-x: auto;
  font-size: 14px;
  line-height: 1.45;
}

/* 代码块内的代码样式 */
:deep(.hljs code) {
  background: transparent;
  padding: 0;
  border-radius: 0;
}

/* 内联代码样式 */
:deep(code:not(pre code)) {
  background: rgba(0, 0, 0, 0.05);
  padding: 2px 6px;
  border-radius: 3px;
  font-size: 0.9em;
  font-family: 'SF Mono', 'Monaco', 'Inconsolata', 'Roboto Mono', monospace;
}

/* Markdown 基础样式 */
:deep(h1),
:deep(h2),
:deep(h3),
:deep(h4),
:deep(h5),
:deep(h6) {
  margin-top: 24px;
  margin-bottom: 16px;
  font-weight: 600;
  line-height: 1.25;
}

:deep(h1) {
  font-size: 2em;
  border-bottom: 1px solid #e1e4e8;
  padding-bottom: 0.3em;
}

:deep(h2) {
  font-size: 1.5em;
  border-bottom: 1px solid #e1e4e8;
  padding-bottom: 0.3em;
}

:deep(h3) {
  font-size: 1.25em;
}

:deep(h4) {
  font-size: 1em;
}

:deep(p) {
  margin-bottom: 16px;
  line-height: 1.6;
}

:deep(ul),
:deep(ol) {
  margin-bottom: 16px;
  padding-left: 2em;
}

:deep(li) {
  margin-bottom: 4px;
}

:deep(blockquote) {
  margin: 16px 0;
  padding: 0 1em;
  border-left: 4px solid #dfe2e5;
  color: #6a737d;
  background: #f6f8fa;
}

:deep(table) {
  border-collapse: collapse;
  width: 100%;
  margin-bottom: 16px;
}

:deep(th),
:deep(td) {
  border: 1px solid #dfe2e5;
  padding: 6px 13px;
}

:deep(th) {
  background: #f6f8fa;
  font-weight: 600;
}

:deep(tr:nth-child(even)) {
  background: #f6f8fa;
}

:deep(a) {
  color: #0366d6;
  text-decoration: none;
}

:deep(a:hover) {
  text-decoration: underline;
}

:deep(hr) {
  height: 1px;
  background: #e1e4e8;
  border: 0;
  margin: 24px 0;
}

:deep(img) {
  max-width: 100%;
  height: auto;
  border-radius: 6px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  :deep(.hljs) {
    font-size: 12px;
    padding: 12px;
  }

  :deep(h1) {
    font-size: 1.5em;
  }

  :deep(h2) {
    font-size: 1.25em;
  }

  :deep(h3) {
    font-size: 1.1em;
  }
}
</style>