import { api } from './api'

export interface LoginRequest {
  email: string
  password: string
  subdomain: string
}

export interface LoginResponse {
  accessToken: string
  refreshToken: string
  tokenType: string
  expiresIn: number
  user: {
    id: string
    email: string
    firstName: string
    lastName: string
    fullName: string
    role: string
    permissions: string[]
    lastLoginAt?: string
    createdAt: string
  }
}

export interface RefreshTokenRequest {
  refreshToken: string
}

export interface RefreshTokenResponse {
  accessToken: string
  refreshToken: string
  tokenType: string
  expiresIn: number
  user: {
    id: string
    email: string
    firstName: string
    lastName: string
    fullName: string
    role: string
    permissions: string[]
    lastLoginAt?: string
    createdAt: string
  }
}

export const authService = {
  login: async (data: LoginRequest): Promise<LoginResponse> => {
    const response = await api.post('/auth/login', data)
    return response.data
  },

  refreshToken: async (data: RefreshTokenRequest): Promise<RefreshTokenResponse> => {
    const response = await api.post('/auth/refresh', data)
    return response.data
  },

  logout: async (): Promise<void> => {
    await api.post('/auth/logout')
  },

  forgotPassword: async (email: string, subdomain: string): Promise<void> => {
    await api.post('/auth/forgot-password', { email, subdomain })
  },

  resetPassword: async (token: string, newPassword: string): Promise<void> => {
    await api.post('/auth/reset-password', { token, newPassword })
  }
}