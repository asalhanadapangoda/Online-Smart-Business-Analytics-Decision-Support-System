import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { customersApi, recommendationsApi } from '@/services/api';
import { Plus, Users, Brain, X, Sparkles } from 'lucide-react';
import toast from 'react-hot-toast';
import { useAuthStore } from '@/store/authStore';

export default function Customers() {
  const { user } = useAuthStore();
  const isCashier = user?.role === 'CASHIER';
  const qc = useQueryClient();
  const [form, setForm] = useState({ name: '', email: '', phoneNumber: '', address: '' });
  const [showForm, setShowForm] = useState(isCashier);

  // AI Churn Prediction State
  const [activeChurnCustomerId, setActiveChurnCustomerId] = useState(null);
  const [churnData, setChurnData] = useState(null);
  const [isChurnLoading, setIsChurnLoading] = useState(false);

  const { data, isLoading } = useQuery({ 
    queryKey: ['customers'], 
    queryFn: () => customersApi.getAll().then(r => r.data.data),
    enabled: !isCashier
  });

  const createMutation = useMutation({
    mutationFn: (d) => customersApi.create(d),
    onSuccess: () => { 
      toast.success('Customer added'); 
      qc.invalidateQueries(['customers']); 
      setShowForm(isCashier); 
      setForm({ name: '', email: '', phoneNumber: '', address: '' });
    },
    onError: (err) => {
      const msg = err.response?.data?.message || 'Check your inputs';
      toast.error('Validation failed: ' + msg);
    }
  });

  const fetchChurnPrediction = async (customerId) => {
    setActiveChurnCustomerId(customerId);
    setIsChurnLoading(true);
    setChurnData(null);
    try {
      const res = await recommendationsApi.getChurn(customerId, user?.branchId || 1);
      setChurnData(res.data.data);
    } catch (err) {
      toast.error('Failed to analyze churn prediction');
      setActiveChurnCustomerId(null);
    } finally {
      setIsChurnLoading(false);
    }
  };

  const handleSaveCustomer = () => {
    if (!form.name || !form.phoneNumber) {
      return toast.error('Name and Phone are required');
    }
    createMutation.mutate({
      ...form,
      branchId: user?.branchId || 1
    });
  };

  const customers = data || [];

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">Customers</h1>
          <p className="page-subtitle">{isCashier ? 'Register a new customer' : `${customers.length} registered customers`}</p>
        </div>
        {!isCashier && (
          <button className="btn btn-primary" onClick={() => setShowForm(!showForm)}>
            <Plus size={16} /> Add Customer
          </button>
        )}
      </div>

      {showForm && (
        <div className="card" style={{ marginBottom: '1.5rem' }}>
          <h3 style={{ fontWeight: 700, marginBottom: '1rem' }}>New Customer</h3>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit,minmax(180px,1fr))', gap: '1rem', marginBottom: '1rem' }}>
            <div><label>Full Name</label><input className="input" value={form.name} onChange={e => setForm({ ...form, name: e.target.value })} /></div>
            <div><label>Email</label><input type="email" className="input" value={form.email} onChange={e => setForm({ ...form, email: e.target.value })} /></div>
            <div><label>Phone</label><input className="input" value={form.phoneNumber} onChange={e => setForm({ ...form, phoneNumber: e.target.value })} /></div>
            <div><label>Address</label><input className="input" value={form.address} onChange={e => setForm({ ...form, address: e.target.value })} /></div>
          </div>
          <button className="btn btn-primary" onClick={handleSaveCustomer} disabled={createMutation.isPending}>
            {createMutation.isPending ? 'Saving...' : 'Save Customer'}
          </button>
        </div>
      )}

      {!isCashier && (
        <div className="card" style={{ padding: 0 }}>
          <div className="table-wrapper">
            {isLoading ? (
              <div style={{ padding: '2rem', textAlign: 'center', color: 'var(--color-text-muted)' }}>Loading...</div>
            ) : customers.length === 0 ? (
              <div style={{ padding: '3rem', textAlign: 'center', color: 'var(--color-text-muted)' }}>
                <Users size={48} style={{ margin: '0 auto 1rem', opacity: 0.3 }} />
                <p>No customers yet.</p>
              </div>
            ) : (
              <table>
                <thead>
                  <tr>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Phone</th>
                    <th>Points</th>
                    <th>Address</th>
                    <th style={{ textAlign: 'right' }}>AI Insights</th>
                  </tr>
                </thead>
                <tbody>
                  {customers.map(c => (
                    <tr key={c.id}>
                      <td style={{ fontWeight: 600 }}>{c.name}</td>
                      <td style={{ color: 'var(--color-text-muted)', fontSize: '0.875rem' }}>{c.email || '—'}</td>
                      <td>{c.phoneNumber}</td>
                      <td>
                        <span className="badge badge-success">{c.loyaltyPoints || 0}</span>
                      </td>
                      <td style={{ color: 'var(--color-text-muted)', fontSize: '0.875rem' }}>{c.address || '—'}</td>
                      <td style={{ textAlign: 'right' }}>
                        <button 
                          className="btn btn-secondary" 
                          style={{ padding: '0.4rem 0.6rem', fontSize: '0.75rem', display: 'inline-flex', alignItems: 'center', gap: '0.25rem' }} 
                          onClick={() => fetchChurnPrediction(c.id)}
                        >
                          <Brain size={14} color="var(--color-primary)" />
                          Predict Churn
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        </div>
      )}

      {/* AI Churn Prediction Modal */}
      {activeChurnCustomerId && (
        <div style={{
          position: 'fixed',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          background: 'rgba(0,0,0,0.5)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          zIndex: 1000,
          backdropFilter: 'blur(4px)'
        }}>
          <div className="card" style={{
            width: '450px',
            padding: '2rem',
            position: 'relative',
            boxShadow: '0 20px 25px -5px rgb(0 0 0 / 0.1), 0 8px 10px -6px rgb(0 0 0 / 0.1)',
            background: 'var(--color-surface)',
            border: '1px solid var(--color-border)',
            borderRadius: '1rem'
          }}>
            <button 
              onClick={() => setActiveChurnCustomerId(null)} 
              style={{
                position: 'absolute',
                top: '1rem',
                right: '1rem',
                background: 'none',
                border: 'none',
                cursor: 'pointer',
                color: 'var(--color-text-muted)'
              }}
            >
              <X size={20} />
            </button>

            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '1.5rem' }}>
              <Brain size={24} color="var(--color-primary)" />
              <h3 style={{ fontWeight: 800, margin: 0 }}>AI Customer Insights</h3>
            </div>

            {isChurnLoading ? (
              <div style={{ padding: '3rem 0', textAlign: 'center' }}>
                <div style={{
                  width: '40px',
                  height: '40px',
                  border: '4px solid var(--color-surface-2)',
                  borderTop: '4px solid var(--color-primary)',
                  borderRadius: '50%',
                  animation: 'spin 1s linear infinite',
                  margin: '0 auto 1rem'
                }} />
                <p style={{ color: 'var(--color-text-muted)', fontSize: '0.9rem' }}>Running ML prediction algorithm...</p>
              </div>
            ) : churnData ? (
              <div>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem', padding: '1rem', background: 'var(--color-surface-2)', borderRadius: '0.75rem' }}>
                  <div>
                    <span style={{ fontSize: '0.75rem', color: 'var(--color-text-muted)', fontWeight: 600 }}>CHURN RISK LEVEL</span>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginTop: '0.25rem' }}>
                      <span className={`badge ${
                        churnData.risk_level === 'HIGH' ? 'badge-danger' : 
                        churnData.risk_level === 'MEDIUM' ? 'badge-warning' : 'badge-success'
                      }`} style={{ fontSize: '0.9rem', padding: '0.3rem 0.7rem' }}>
                        {churnData.risk_level}
                      </span>
                    </div>
                  </div>
                  <div style={{ textAlign: 'right' }}>
                    <span style={{ fontSize: '0.75rem', color: 'var(--color-text-muted)', fontWeight: 600 }}>PROBABILITY</span>
                    <div style={{ fontSize: '1.5rem', fontWeight: 900, color: 'var(--color-primary)', marginTop: '0.1rem' }}>
                      {(churnData.churn_probability * 100).toFixed(1)}%
                    </div>
                  </div>
                </div>

                {/* Progress bar */}
                <div style={{ height: '8px', background: 'var(--color-surface-2)', borderRadius: '4px', overflow: 'hidden', marginBottom: '2rem' }}>
                  <div style={{
                    width: `${churnData.churn_probability * 100}%`,
                    height: '100%',
                    background: churnData.risk_level === 'HIGH' ? 'var(--color-danger)' : 
                               churnData.risk_level === 'MEDIUM' ? 'var(--color-warning)' : 'var(--color-success)',
                    transition: 'width 1s ease-in-out'
                  }} />
                </div>

                <div style={{ background: 'linear-gradient(135deg, rgba(99,102,241,0.05), rgba(99,102,241,0.02))', border: '1px solid rgba(99,102,241,0.15)', padding: '1.25rem', borderRadius: '0.75rem', display: 'flex', gap: '0.75rem' }}>
                  <Sparkles size={20} color="var(--color-primary)" style={{ flexShrink: 0, marginTop: '0.1rem' }} />
                  <div>
                    <span style={{ fontWeight: 800, fontSize: '0.85rem', color: 'var(--color-primary)', textTransform: 'uppercase', letterSpacing: '0.05em' }}>AI Retention Strategy</span>
                    <p style={{ fontSize: '0.9rem', lineHeight: 1.5, marginTop: '0.25rem' }}>{churnData.recommendation}</p>
                  </div>
                </div>

                <button 
                  className="btn btn-primary" 
                  onClick={() => setActiveChurnCustomerId(null)} 
                  style={{ width: '100%', marginTop: '2rem', padding: '0.75rem' }}
                >
                  Close Insights
                </button>
              </div>
            ) : null}
          </div>
        </div>
      )}
    </div>
  );
}
