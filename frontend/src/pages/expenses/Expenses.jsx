import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { expensesApi, expenseCategoriesApi, branchesApi } from '@/services/api';
import { Plus, Trash2, DollarSign, Settings } from 'lucide-react';
import toast from 'react-hot-toast';
import { useAuthStore } from '@/store/authStore';

const fmt = (n) => new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(n || 0);

export default function Expenses() {
  const qc = useQueryClient();
  const { user } = useAuthStore();
  const [form, setForm] = useState({ description: '', amount: '', expenseDate: '', categoryId: '', branchId: '' });
  const [catForm, setCatForm] = useState({ name: '', description: '' });
  const [showForm, setShowForm] = useState(false);
  const [showCatForm, setShowCatForm] = useState(false);

  const { data: expenses = [], isLoading } = useQuery({ queryKey: ['expenses'], queryFn: () => expensesApi.getAll().then(r => r.data.data) });
  const { data: categories = [] } = useQuery({ queryKey: ['expenseCategories'], queryFn: () => expenseCategoriesApi.getAll().then(r => r.data.data) });
  const { data: branches = [] } = useQuery({ queryKey: ['branches'], queryFn: () => branchesApi.getAll().then(r => r.data.data) });

  const createMutation = useMutation({
    mutationFn: (d) => expensesApi.create(d),
    onSuccess: () => { toast.success('Expense added'); qc.invalidateQueries(['expenses']); setShowForm(false); setForm({ description: '', amount: '', expenseDate: '', categoryId: '', branchId: '' }); },
  });

  const createCatMutation = useMutation({
    mutationFn: (d) => expenseCategoriesApi.create(d),
    onSuccess: () => { toast.success('Category created'); qc.invalidateQueries(['expenseCategories']); setShowCatForm(false); setCatForm({ name: '', description: '' }); },
  });

  const deleteMutation = useMutation({
    mutationFn: (id) => expensesApi.delete(id),
    onSuccess: () => { toast.success('Expense deleted'); qc.invalidateQueries(['expenses']); },
  });

  const total = expenses.reduce((s, e) => s + (parseFloat(e.amount) || 0), 0);

  return (
    <div>
      <div className="page-header">
        <div><h1 className="page-title">Expenses</h1><p className="page-subtitle">Total: {fmt(total)} this period</p></div>
        <div style={{ display: 'flex', gap: '0.5rem' }}>
          {user?.role === 'ADMIN' && (
            <button className="btn btn-secondary" onClick={() => setShowCatForm(!showCatForm)}><Settings size={16} /> Categories</button>
          )}
          <button className="btn btn-primary" onClick={() => setShowForm(!showForm)}><Plus size={16} /> Add Expense</button>
        </div>
      </div>

      {showCatForm && user?.role === 'ADMIN' && (
        <div className="card" style={{ marginBottom: '1.5rem', borderLeft: '4px solid var(--color-primary)' }}>
          <h3 style={{ fontWeight: 700, marginBottom: '1rem' }}>New Expense Category (Admin Only)</h3>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit,minmax(180px,1fr))', gap: '1rem', marginBottom: '1rem' }}>
            <div><label>Category Name</label><input className="input" value={catForm.name} onChange={e => setCatForm({ ...catForm, name: e.target.value })} placeholder="e.g. Rent" /></div>
            <div><label>Description</label><input className="input" value={catForm.description} onChange={e => setCatForm({ ...catForm, description: e.target.value })} placeholder="Optional description" /></div>
          </div>
          <button className="btn btn-primary" onClick={() => createCatMutation.mutate(catForm)} disabled={createCatMutation.isPending}>{createCatMutation.isPending ? 'Saving...' : 'Create Category'}</button>
        </div>
      )}

      {showForm && (
        <div className="card" style={{ marginBottom: '1.5rem' }}>
          <h3 style={{ fontWeight: 700, marginBottom: '1rem' }}>New Expense</h3>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit,minmax(180px,1fr))', gap: '1rem', marginBottom: '1rem' }}>
            <div><label>Description</label><input className="input" value={form.description} onChange={e => setForm({ ...form, description: e.target.value })} placeholder="Expense description" /></div>
            <div><label>Amount</label><input className="input" type="number" value={form.amount} onChange={e => setForm({ ...form, amount: e.target.value })} placeholder="0.00" /></div>
            <div><label>Date</label><input type="date" className="input" value={form.expenseDate} onChange={e => setForm({ ...form, expenseDate: e.target.value })} /></div>
            <div>
              <label>Category</label>
              <select className="input" value={form.categoryId} onChange={e => setForm({ ...form, categoryId: e.target.value })}>
                <option value="">Select Category</option>
                {categories.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
              </select>
            </div>
            <div>
              <label>Branch</label>
              <select className="input" value={form.branchId} onChange={e => setForm({ ...form, branchId: e.target.value })}>
                <option value="">Select Branch</option>
                {branches.map(b => <option key={b.id} value={b.id}>{b.name}</option>)}
              </select>
            </div>
          </div>
          <button className="btn btn-primary" onClick={() => createMutation.mutate(form)} disabled={createMutation.isPending}>{createMutation.isPending ? 'Saving...' : 'Save Expense'}</button>
        </div>
      )}

      <div className="card" style={{ padding: 0 }}>
        <div className="table-wrapper">
          {isLoading ? <div style={{ padding: '2rem', textAlign: 'center', color: 'var(--color-text-muted)' }}>Loading...</div>
            : expenses.length === 0 ? <div style={{ padding: '3rem', textAlign: 'center', color: 'var(--color-text-muted)' }}><DollarSign size={48} style={{ margin: '0 auto 1rem', opacity: 0.3 }} /><p>No expenses recorded.</p></div>
            : <table>
                <thead><tr><th>Description</th><th>Amount</th><th>Date</th><th>Category</th><th>Branch</th><th>Actions</th></tr></thead>
                <tbody>{expenses.map(e => (
                  <tr key={e.id}>
                    <td style={{ fontWeight: 500 }}>{e.description}</td>
                    <td style={{ fontWeight: 600, color: 'var(--color-danger)' }}>{fmt(e.amount)}</td>
                    <td style={{ fontSize: '0.8rem', color: 'var(--color-text-muted)' }}>{e.expenseDate}</td>
                    <td>{e.categoryName || '—'}</td>
                    <td>{e.branchName || '—'}</td>
                    <td><button className="btn btn-danger" style={{ padding: '0.3rem 0.6rem', fontSize: '0.75rem' }} onClick={() => { if (confirm('Delete expense?')) deleteMutation.mutate(e.id); }}><Trash2 size={14} /></button></td>
                  </tr>
                ))}</tbody>
              </table>}
        </div>
      </div>
    </div>
  );
}
