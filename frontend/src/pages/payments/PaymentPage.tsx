import { useParams } from 'react-router-dom'
import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { api } from '../../services/api'
import { 
  CreditCard, 
  CheckCircle, 
  XCircle,
  Lock,
  Shield
} from 'lucide-react'

interface PaymentData {
  orderId: string
  totalAmount: number
  currency: string
  selectedPhotos: Array<{
    id: string
    filename: string
    price: number
  }>
  clientInfo: {
    name: string
    email: string
    phone?: string
  }
}

export default function PaymentPage() {
  const { orderId } = useParams<{ orderId: string }>()
  const [paymentMethod, setPaymentMethod] = useState('stripe')
  const [isProcessing, setIsProcessing] = useState(false)
  const [paymentStatus, setPaymentStatus] = useState<'pending' | 'success' | 'error'>('pending')

  const { data: paymentData, isLoading } = useQuery<PaymentData>({
    queryKey: ['payment', orderId],
    queryFn: async () => {
      const response = await api.get(`/payments/${orderId}`)
      return response.data
    },
    enabled: !!orderId
  })

  const handlePayment = async () => {
    if (!paymentData) return

    setIsProcessing(true)
    try {
      // This would integrate with actual payment processing
      const response = await api.post('/payments/process', {
        orderId: paymentData.orderId,
        amount: paymentData.totalAmount,
        currency: paymentData.currency,
        paymentMethod,
        selectedPhotos: paymentData.selectedPhotos.map(photo => photo.id)
      })

      if (response.data.success) {
        setPaymentStatus('success')
      } else {
        setPaymentStatus('error')
      }
    } catch (error) {
      setPaymentStatus('error')
    } finally {
      setIsProcessing(false)
    }
  }

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">Loading payment information...</p>
        </div>
      </div>
    )
  }

  if (!paymentData) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="text-center">
          <XCircle className="mx-auto h-12 w-12 text-red-400" />
          <h3 className="mt-2 text-lg font-medium text-gray-900">Payment not found</h3>
          <p className="mt-1 text-sm text-gray-500">
            The payment information you're looking for doesn't exist.
          </p>
        </div>
      </div>
    )
  }

  if (paymentStatus === 'success') {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="text-center">
          <CheckCircle className="mx-auto h-16 w-16 text-green-500" />
          <h3 className="mt-4 text-2xl font-bold text-gray-900">Payment Successful!</h3>
          <p className="mt-2 text-gray-600">
            Your photos are now available for download.
          </p>
          <div className="mt-6">
            <button className="inline-flex items-center px-6 py-3 border border-transparent rounded-md shadow-sm text-base font-medium text-white bg-blue-600 hover:bg-blue-700">
              Download Photos
            </button>
          </div>
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-gray-50 py-12">
      <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="bg-white shadow rounded-lg">
          <div className="px-4 py-5 sm:p-6">
            <h1 className="text-2xl font-bold text-gray-900 mb-6">Complete Your Purchase</h1>

            <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
              {/* Order Summary */}
              <div>
                <h2 className="text-lg font-medium text-gray-900 mb-4">Order Summary</h2>
                <div className="space-y-4">
                  <div>
                    <h3 className="text-sm font-medium text-gray-700">Selected Photos</h3>
                    <div className="mt-2 space-y-2">
                      {paymentData.selectedPhotos.map((photo) => (
                        <div key={photo.id} className="flex justify-between text-sm">
                          <span className="text-gray-600">{photo.filename}</span>
                          <span className="font-medium">${photo.price}</span>
                        </div>
                      ))}
                    </div>
                  </div>
                  <div className="border-t pt-4">
                    <div className="flex justify-between text-lg font-medium">
                      <span>Total</span>
                      <span>${paymentData.totalAmount.toFixed(2)} {paymentData.currency.toUpperCase()}</span>
                    </div>
                  </div>
                </div>
              </div>

              {/* Payment Form */}
              <div>
                <h2 className="text-lg font-medium text-gray-900 mb-4">Payment Information</h2>
                
                {/* Payment Method Selection */}
                <div className="space-y-3 mb-6">
                  <label className="block text-sm font-medium text-gray-700">Payment Method</label>
                  <div className="space-y-2">
                    <label className="flex items-center">
                      <input
                        type="radio"
                        value="stripe"
                        checked={paymentMethod === 'stripe'}
                        onChange={(e) => setPaymentMethod(e.target.value)}
                        className="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300"
                      />
                      <span className="ml-2 text-sm text-gray-700">Credit Card (Stripe)</span>
                    </label>
                    <label className="flex items-center">
                      <input
                        type="radio"
                        value="razorpay"
                        checked={paymentMethod === 'razorpay'}
                        onChange={(e) => setPaymentMethod(e.target.value)}
                        className="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300"
                      />
                      <span className="ml-2 text-sm text-gray-700">Razorpay</span>
                    </label>
                  </div>
                </div>

                {/* Payment Form Fields */}
                <div className="space-y-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-700">Card Number</label>
                    <input
                      type="text"
                      placeholder="1234 5678 9012 3456"
                      className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
                    />
                  </div>
                  <div className="grid grid-cols-2 gap-4">
                    <div>
                      <label className="block text-sm font-medium text-gray-700">Expiry Date</label>
                      <input
                        type="text"
                        placeholder="MM/YY"
                        className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
                      />
                    </div>
                    <div>
                      <label className="block text-sm font-medium text-gray-700">CVC</label>
                      <input
                        type="text"
                        placeholder="123"
                        className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
                      />
                    </div>
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700">Cardholder Name</label>
                    <input
                      type="text"
                      placeholder="John Doe"
                      className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
                    />
                  </div>
                </div>

                {/* Security Notice */}
                <div className="mt-6 p-4 bg-blue-50 rounded-md">
                  <div className="flex">
                    <Shield className="h-5 w-5 text-blue-400" />
                    <div className="ml-3">
                      <h3 className="text-sm font-medium text-blue-800">Secure Payment</h3>
                      <p className="mt-1 text-sm text-blue-700">
                        Your payment information is encrypted and secure. We never store your card details.
                      </p>
                    </div>
                  </div>
                </div>

                {/* Payment Button */}
                <button
                  onClick={handlePayment}
                  disabled={isProcessing}
                  className="mt-6 w-full flex justify-center items-center px-4 py-3 border border-transparent rounded-md shadow-sm text-base font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  {isProcessing ? (
                    <>
                      <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
                      Processing...
                    </>
                  ) : (
                    <>
                      <Lock className="h-4 w-4 mr-2" />
                      Pay ${paymentData.totalAmount.toFixed(2)}
                    </>
                  )}
                </button>

                {paymentStatus === 'error' && (
                  <div className="mt-4 p-4 bg-red-50 rounded-md">
                    <div className="flex">
                      <XCircle className="h-5 w-5 text-red-400" />
                      <div className="ml-3">
                        <h3 className="text-sm font-medium text-red-800">Payment Failed</h3>
                        <p className="mt-1 text-sm text-red-700">
                          There was an error processing your payment. Please try again.
                        </p>
                      </div>
                    </div>
                  </div>
                )}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}