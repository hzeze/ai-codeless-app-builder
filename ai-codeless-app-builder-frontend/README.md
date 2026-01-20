# ai-codeless-app-builder-frontend

This template should help get you started developing with Vue 3 in Vite.

## Recommended IDE Setup

[VSCode](https://code.visualstudio.com/) + [Volar](https://marketplace.visualstudio.com/items?itemName=Vue.volar) (and disable Vetur).

## Type Support for `.vue` Imports in TS

TypeScript cannot handle type information for `.vue` imports by default, so we replace the `tsc` CLI with `vue-tsc` for type checking. In editors, we need [Volar](https://marketplace.visualstudio.com/items?itemName=Vue.volar) to make the TypeScript language service aware of `.vue` types.

## Customize configuration

See [Vite Configuration Reference](https://vite.dev/config/).

## Environment Variables

Vite supports multiple environment variable files for different environments:

- `.env` - Loaded in all environments
- `.env.local` - Local overrides (highest priority, not committed to git)
- `.env.development` - Development environment
- `.env.production` - Production environment

### Development Environment (.env.development)

```env
# 开发环境配置
VITE_API_BASE_URL=http://localhost:8123/api
VITE_APP_DEPLOY_DOMAIN=http://localhost
```

### Production Environment (.env.production)

```env
# 生产环境配置
VITE_API_BASE_URL=https://api.yourdomain.com/api
VITE_APP_DEPLOY_DOMAIN=https://apps.yourdomain.com
```

### Local Overrides (.env.local)

For local development overrides that shouldn't be committed:

```env
# 本地开发环境覆盖配置（不会提交到版本控制）
VITE_API_BASE_URL=http://localhost:8123/api
VITE_APP_DEPLOY_DOMAIN=http://localhost
```

**Note:** `.env.local` files are not included in version control. Other `.env.*` files can be committed if needed, but typically only `.env.example` is committed as a template.

The application uses `src/env.ts` to manage environment variables. All environment variables are imported from this file and have default fallback values for development.

### Environment Variable Descriptions

- `VITE_API_BASE_URL`: Backend API base URL, used for all API requests (default: `http://localhost:8123/api`)
- `VITE_APP_DEPLOY_DOMAIN`: Domain where deployed applications are hosted (default: `http://localhost`)
- `APP_PREVIEW_DOMAIN`: Automatically calculated as `${API_BASE_URL}/static` for previewing generated applications

## Project Setup

```sh
npm install
```

### Compile and Hot-Reload for Development

```sh
npm run dev
```

### Type-Check, Compile and Minify for Production

```sh
npm run build
```

### Lint with [ESLint](https://eslint.org/)

```sh
npm run lint
```
