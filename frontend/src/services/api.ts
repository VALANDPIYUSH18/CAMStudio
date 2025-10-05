import axios from 'axios'
import toast from 'react-hot-toast'

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api'

export const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// Request interceptor
api.interceptors.request.use(
  (config) => {
    // Add any request modifications here
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor
api.interceptors.response.use(
  (response) => {
    return response
  },
  async (error) => {
    const originalRequest = error.config

    // Handle 401 errors (unauthorized)
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true

      try {
        // Try to refresh token
        const refreshToken = localStorage.getItem('auth-storage')
        if (refreshToken) {
          const authData = JSON.parse(refreshToken)
          if (authData.state?.refreshToken) {
            const response = await api.post('/auth/refresh', {
              refreshToken: authData.state.refreshToken
            })

            const { accessToken, refreshToken: newRefreshToken } = response.data
            
            // Update stored tokens
            const updatedAuthData = {
              ...authData,
              state: {
                ...authData.state,
                accessToken,
                refreshToken: newRefreshToken || authData.state.refreshToken
              }
            }
            localStorage.setItem('auth-storage', JSON.stringify(updatedAuthData))

            // Update authorization header
            api.defaults.headers.common['Authorization'] = `Bearer ${accessToken}`

            // Retry original request
            originalRequest.headers['Authorization'] = `Bearer ${accessToken}`
            return api(originalRequest)
          }
        }
      } catch (refreshError) {
        // Refresh failed, redirect to login
        localStorage.removeItem('auth-storage')
        window.location.href = '/login'
        return Promise.reject(refreshError)
      }
    }

    // Handle other errors
    if (error.response?.status >= 500) {
      toast.error('Server error. Please try again later.')
    } else if (error.response?.status === 403) {
      toast.error('You do not have permission to perform this action.')
    } else if (error.response?.status === 404) {
      toast.error('Resource not found.')
    } else if (error.response?.data?.message) {
      toast.error(error.response.data.message)
    } else if (error.message) {
      toast.error(error.message)
    }

    return Promise.reject(error)
  }
)

export default api