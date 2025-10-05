import { create } from 'zustand'
import { persist } from 'zustand/middleware'
import { api } from '../services/api'

export interface User {
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

export interface AuthState {
  user: User | null
  accessToken: string | null
  refreshToken: string | null
  isAuthenticated: boolean
  isLoading: boolean
  error: string | null
}

export interface AuthActions {
  login: (email: string, password: string, subdomain: string) => Promise<void>
  logout: () => void
  refreshToken: () => Promise<void>
  checkAuth: () => void
  clearError: () => void
  setUser: (user: User) => void
  setTokens: (accessToken: string, refreshToken: string) => void
}

export const useAuthStore = create<AuthState & AuthActions>()(
  persist(
    (set, get) => ({
      // State
      user: null,
      accessToken: null,
      refreshToken: null,
      isAuthenticated: false,
      isLoading: false,
      error: null,

      // Actions
      login: async (email: string, password: string, subdomain: string) => {
        set({ isLoading: true, error: null })
        
        try {
          const response = await api.post('/auth/login', {
            email,
            password,
            subdomain
          })

          const { accessToken, refreshToken, user } = response.data
          
          set({
            user,
            accessToken,
            refreshToken,
            isAuthenticated: true,
            isLoading: false,
            error: null
          })

          // Set default authorization header
          api.defaults.headers.common['Authorization'] = `Bearer ${accessToken}`
        } catch (error: any) {
          set({
            user: null,
            accessToken: null,
            refreshToken: null,
            isAuthenticated: false,
            isLoading: false,
            error: error.response?.data?.message || 'Login failed'
          })
          throw error
        }
      },

      logout: () => {
        set({
          user: null,
          accessToken: null,
          refreshToken: null,
          isAuthenticated: false,
          isLoading: false,
          error: null
        })
        
        // Clear authorization header
        delete api.defaults.headers.common['Authorization']
      },

      refreshToken: async () => {
        const { refreshToken } = get()
        
        if (!refreshToken) {
          throw new Error('No refresh token available')
        }

        try {
          const response = await api.post('/auth/refresh', {
            refreshToken
          })

          const { accessToken, newRefreshToken } = response.data
          
          set({
            accessToken,
            refreshToken: newRefreshToken || refreshToken
          })

          // Update authorization header
          api.defaults.headers.common['Authorization'] = `Bearer ${accessToken}`
        } catch (error) {
          // If refresh fails, logout user
          get().logout()
          throw error
        }
      },

      checkAuth: () => {
        const { accessToken, user } = get()
        
        if (accessToken && user) {
          set({ isAuthenticated: true })
          api.defaults.headers.common['Authorization'] = `Bearer ${accessToken}`
        } else {
          set({ isAuthenticated: false })
          delete api.defaults.headers.common['Authorization']
        }
      },

      clearError: () => {
        set({ error: null })
      },

      setUser: (user: User) => {
        set({ user })
      },

      setTokens: (accessToken: string, refreshToken: string) => {
        set({ accessToken, refreshToken })
        api.defaults.headers.common['Authorization'] = `Bearer ${accessToken}`
      }
    }),
    {
      name: 'auth-storage',
      partialize: (state) => ({
        user: state.user,
        accessToken: state.accessToken,
        refreshToken: state.refreshToken,
        isAuthenticated: state.isAuthenticated
      })
    }
  )
)