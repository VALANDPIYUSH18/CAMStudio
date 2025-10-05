import { useParams } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { api } from '../../services/api'
import { 
  Calendar, 
  User, 
  Camera, 
  DollarSign,
  Edit,
  Upload,
  Share2,
  QrCode
} from 'lucide-react'

interface Order {
  id: string
  title: string
  description?: string
  eventDate?: string
  status: string
  totalAmount?: number
  clientId: string
  photographerId?: string
  galleryUrl?: string
  qrCode?: string
  createdAt: string
  updatedAt: string
}

export default function OrderDetailsPage() {
  const { orderId } = useParams<{ orderId: string }>()

  const { data: order, isLoading } = useQuery<Order>({
    queryKey: ['order', orderId],
    queryFn: async () => {
      const response = await api.get(`/orders/${orderId}`)
      return response.data
    },
    enabled: !!orderId
  })

  const statusColors = {
    CREATED: 'bg-blue-100 text-blue-800',
    IN_PROGRESS: 'bg-yellow-100 text-yellow-800',
    PHOTOS_UPLOADED: 'bg-green-100 text-green-800',
    CLIENT_REVIEW: 'bg-purple-100 text-purple-800',
    PAYMENT_PENDING: 'bg-orange-100 text-orange-800',
    PAID: 'bg-green-100 text-green-800',
    COMPLETED: 'bg-gray-100 text-gray-800',
    CANCELLED: 'bg-red-100 text-red-800'
  }

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
      </div>
    )
  }

  if (!order) {
    return (
      <div className="text-center py-12">
        <h3 className="text-lg font-medium text-gray-900">Order not found</h3>
        <p className="mt-1 text-sm text-gray-500">
          The order you're looking for doesn't exist.
        </p>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-start">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">{order.title}</h1>
          <p className="mt-1 text-sm text-gray-500">
            Order ID: {order.id}
          </p>
        </div>
        <div className="flex space-x-3">
          <button className="inline-flex items-center px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50">
            <Edit className="h-4 w-4 mr-2" />
            Edit
          </button>
          <button className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700">
            <Upload className="h-4 w-4 mr-2" />
            Upload Photos
          </button>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Order Details */}
        <div className="lg:col-span-2 space-y-6">
          <div className="bg-white shadow rounded-lg p-6">
            <h3 className="text-lg font-medium text-gray-900 mb-4">Order Information</h3>
            <dl className="grid grid-cols-1 gap-x-4 gap-y-6 sm:grid-cols-2">
              <div>
                <dt className="text-sm font-medium text-gray-500">Status</dt>
                <dd className="mt-1">
                  <span
                    className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${statusColors[order.status as keyof typeof statusColors]}`}
                  >
                    {order.status.replace('_', ' ')}
                  </span>
                </dd>
              </div>
              <div>
                <dt className="text-sm font-medium text-gray-500">Event Date</dt>
                <dd className="mt-1 text-sm text-gray-900">
                  {order.eventDate ? (
                    <div className="flex items-center">
                      <Calendar className="h-4 w-4 mr-2 text-gray-400" />
                      {new Date(order.eventDate).toLocaleDateString()}
                    </div>
                  ) : (
                    'Not set'
                  )}
                </dd>
              </div>
              <div>
                <dt className="text-sm font-medium text-gray-500">Client ID</dt>
                <dd className="mt-1 text-sm text-gray-900">
                  <div className="flex items-center">
                    <User className="h-4 w-4 mr-2 text-gray-400" />
                    {order.clientId}
                  </div>
                </dd>
              </div>
              <div>
                <dt className="text-sm font-medium text-gray-500">Photographer</dt>
                <dd className="mt-1 text-sm text-gray-900">
                  {order.photographerId ? (
                    <div className="flex items-center">
                      <Camera className="h-4 w-4 mr-2 text-gray-400" />
                      {order.photographerId}
                    </div>
                  ) : (
                    'Not assigned'
                  )}
                </dd>
              </div>
              <div>
                <dt className="text-sm font-medium text-gray-500">Total Amount</dt>
                <dd className="mt-1 text-sm text-gray-900">
                  {order.totalAmount ? (
                    <div className="flex items-center">
                      <DollarSign className="h-4 w-4 mr-2 text-gray-400" />
                      ${order.totalAmount.toFixed(2)}
                    </div>
                  ) : (
                    'Not calculated'
                  )}
                </dd>
              </div>
              <div>
                <dt className="text-sm font-medium text-gray-500">Created</dt>
                <dd className="mt-1 text-sm text-gray-900">
                  {new Date(order.createdAt).toLocaleDateString()}
                </dd>
              </div>
            </dl>
            {order.description && (
              <div className="mt-6">
                <dt className="text-sm font-medium text-gray-500">Description</dt>
                <dd className="mt-1 text-sm text-gray-900">{order.description}</dd>
              </div>
            )}
          </div>

          {/* Gallery Section */}
          <div className="bg-white shadow rounded-lg p-6">
            <h3 className="text-lg font-medium text-gray-900 mb-4">Photo Gallery</h3>
            <div className="text-center py-12">
              <Camera className="mx-auto h-12 w-12 text-gray-400" />
              <h3 className="mt-2 text-sm font-medium text-gray-900">No photos uploaded</h3>
              <p className="mt-1 text-sm text-gray-500">
                Upload photos to get started with this order.
              </p>
              <div className="mt-6">
                <button className="inline-flex items-center px-4 py-2 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700">
                  <Upload className="h-4 w-4 mr-2" />
                  Upload Photos
                </button>
              </div>
            </div>
          </div>
        </div>

        {/* Sidebar */}
        <div className="space-y-6">
          {/* Client Access */}
          <div className="bg-white shadow rounded-lg p-6">
            <h3 className="text-lg font-medium text-gray-900 mb-4">Client Access</h3>
            {order.galleryUrl ? (
              <div className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700">Gallery URL</label>
                  <div className="mt-1 flex rounded-md shadow-sm">
                    <input
                      type="text"
                      value={order.galleryUrl}
                      readOnly
                      className="flex-1 min-w-0 block w-full px-3 py-2 rounded-none rounded-l-md border border-gray-300 bg-gray-50 text-sm"
                    />
                    <button className="inline-flex items-center px-3 py-2 border border-l-0 border-gray-300 rounded-r-md bg-gray-50 text-gray-500 text-sm hover:bg-gray-100">
                      <Share2 className="h-4 w-4" />
                    </button>
                  </div>
                </div>
                {order.qrCode && (
                  <div>
                    <label className="block text-sm font-medium text-gray-700">QR Code</label>
                    <div className="mt-1 p-4 border border-gray-300 rounded-md bg-gray-50 text-center">
                      <QrCode className="mx-auto h-16 w-16 text-gray-400" />
                      <p className="mt-2 text-xs text-gray-500">QR Code for client access</p>
                    </div>
                  </div>
                )}
              </div>
            ) : (
              <p className="text-sm text-gray-500">Gallery not yet created</p>
            )}
          </div>

          {/* Quick Actions */}
          <div className="bg-white shadow rounded-lg p-6">
            <h3 className="text-lg font-medium text-gray-900 mb-4">Quick Actions</h3>
            <div className="space-y-3">
              <button className="w-full inline-flex items-center justify-center px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50">
                <Edit className="h-4 w-4 mr-2" />
                Edit Order
              </button>
              <button className="w-full inline-flex items-center justify-center px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50">
                <Upload className="h-4 w-4 mr-2" />
                Upload Photos
              </button>
              <button className="w-full inline-flex items-center justify-center px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50">
                <Share2 className="h-4 w-4 mr-2" />
                Share Gallery
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}