import { useQuery } from '@tanstack/react-query'
import { api } from '../../services/api'
import { 
  FolderOpen, 
  Camera, 
  DollarSign, 
  Users,
  TrendingUp,
  Clock
} from 'lucide-react'

interface DashboardStats {
  totalOrders: number
  totalPhotos: number
  totalRevenue: number
  totalClients: number
  recentOrders: Array<{
    id: string
    title: string
    status: string
    createdAt: string
  }>
}

export default function DashboardPage() {
  const { data: stats, isLoading } = useQuery<DashboardStats>({
    queryKey: ['dashboard-stats'],
    queryFn: async () => {
      const response = await api.get('/analytics/dashboard')
      return response.data
    }
  })

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
      </div>
    )
  }

  const statCards = [
    {
      name: 'Total Orders',
      value: stats?.totalOrders || 0,
      icon: FolderOpen,
      color: 'bg-blue-500'
    },
    {
      name: 'Total Photos',
      value: stats?.totalPhotos || 0,
      icon: Camera,
      color: 'bg-green-500'
    },
    {
      name: 'Total Revenue',
      value: `$${stats?.totalRevenue || 0}`,
      icon: DollarSign,
      color: 'bg-yellow-500'
    },
    {
      name: 'Total Clients',
      value: stats?.totalClients || 0,
      icon: Users,
      color: 'bg-purple-500'
    }
  ]

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Dashboard</h1>
        <p className="mt-1 text-sm text-gray-500">
          Overview of your photography studio
        </p>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4">
        {statCards.map((stat) => {
          const Icon = stat.icon
          return (
            <div
              key={stat.name}
              className="bg-white overflow-hidden shadow rounded-lg"
            >
              <div className="p-5">
                <div className="flex items-center">
                  <div className="flex-shrink-0">
                    <div className={`p-3 rounded-md ${stat.color}`}>
                      <Icon className="h-6 w-6 text-white" />
                    </div>
                  </div>
                  <div className="ml-5 w-0 flex-1">
                    <dl>
                      <dt className="text-sm font-medium text-gray-500 truncate">
                        {stat.name}
                      </dt>
                      <dd className="text-lg font-medium text-gray-900">
                        {stat.value}
                      </dd>
                    </dl>
                  </div>
                </div>
              </div>
            </div>
          )
        })}
      </div>

      {/* Recent Orders */}
      <div className="bg-white shadow rounded-lg">
        <div className="px-4 py-5 sm:p-6">
          <h3 className="text-lg leading-6 font-medium text-gray-900 mb-4">
            Recent Orders
          </h3>
          <div className="flow-root">
            <ul className="-my-5 divide-y divide-gray-200">
              {stats?.recentOrders?.map((order) => (
                <li key={order.id} className="py-4">
                  <div className="flex items-center space-x-4">
                    <div className="flex-shrink-0">
                      <div className="h-8 w-8 rounded-full bg-blue-100 flex items-center justify-center">
                        <FolderOpen className="h-4 w-4 text-blue-600" />
                      </div>
                    </div>
                    <div className="flex-1 min-w-0">
                      <p className="text-sm font-medium text-gray-900 truncate">
                        {order.title}
                      </p>
                      <p className="text-sm text-gray-500">
                        Status: {order.status}
                      </p>
                    </div>
                    <div className="flex-shrink-0 text-sm text-gray-500">
                      <Clock className="h-4 w-4 inline mr-1" />
                      {new Date(order.createdAt).toLocaleDateString()}
                    </div>
                  </div>
                </li>
              ))}
            </ul>
          </div>
        </div>
      </div>
    </div>
  )
}