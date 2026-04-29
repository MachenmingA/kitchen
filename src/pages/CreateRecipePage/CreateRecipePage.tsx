import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ChefHat, Plus, Trash2, Save, ArrowLeft, Image, Upload } from 'lucide-react';
import { recipeApi, getCurrentUser } from '../../services/api';
import './CreateRecipePage.css';

const API_BASE = '/api';

interface Ingredient {
  name: string;
  amount: string;
}

interface Step {
  stepNumber: number;
  content: string;
  imageUrl: string;
}

const CATEGORIES = ['家常菜', '快手菜', '烘焙甜点', '早餐', '午餐', '晚餐', '小吃', '饮品', '汤粥', '凉菜'];
const DIFFICULTIES = ['简单', '中等', '困难'];

export const CreateRecipePage = () => {
  const navigate = useNavigate();
  const user = getCurrentUser();
  const [loading, setLoading] = useState(false);
  const [uploadingImage, setUploadingImage] = useState(false);
  const [uploadingStepImage, setUploadingStepImage] = useState<number | null>(null);
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    imageUrl: '',
    category: CATEGORIES[0],
    difficulty: DIFFICULTIES[0],
    cookTime: 30,
    servings: 2,
  });
  const [ingredients, setIngredients] = useState<Ingredient[]>([
    { name: '', amount: '' }
  ]);
  const [steps, setSteps] = useState<Step[]>([
    { stepNumber: 1, content: '', imageUrl: '' }
  ]);

  const getAuthHeaders = () => {
    const token = localStorage.getItem('accessToken');
    const headers: Record<string, string> = {};
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }
    return headers;
  };

  const uploadImage = async (file: File): Promise<string> => {
    const formData = new FormData();
    formData.append('file', file);

    const response = await fetch(`${API_BASE}/upload/image`, {
      method: 'POST',
      headers: getAuthHeaders(),
      body: formData,
    });

    const result = await response.json();
    if (result.code !== 200) {
      throw new Error(result.message || '上传失败');
    }
    return result.data.url;
  };

  const handleCoverImageChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    setUploadingImage(true);
    try {
      const url = await uploadImage(file);
      setFormData({ ...formData, imageUrl: url });
    } catch (err: any) {
      alert(err.message || '封面上传失败');
    } finally {
      setUploadingImage(false);
    }
  };

  const handleStepImageChange = async (index: number, e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    setUploadingStepImage(index);
    try {
      const url = await uploadImage(file);
      const updated = [...steps];
      updated[index].imageUrl = url;
      setSteps(updated);
    } catch (err: any) {
      alert(err.message || '步骤图片上传失败');
    } finally {
      setUploadingStepImage(null);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!formData.title.trim()) {
      alert('请输入食谱标题');
      return;
    }
    
    const validIngredients = ingredients.filter(i => i.name.trim() && i.amount.trim());
    const validSteps = steps.filter(s => s.content.trim());
    
    if (validSteps.length === 0) {
      alert('请至少添加一个步骤');
      return;
    }

    setLoading(true);

    try {
      const recipe = {
        title: formData.title,
        description: formData.description,
        imageUrl: formData.imageUrl || 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=800',
        category: formData.category,
        difficulty: formData.difficulty,
        cookTime: formData.cookTime,
        servings: formData.servings,
        authorId: user?.id || 1,
        authorName: user?.nickname || user?.username || '匿名',
        authorAvatar: user?.avatar || '',
      };

      const formattedIngredients = validIngredients.map((ing, idx) => ({
        name: ing.name,
        amount: ing.amount,
        sortOrder: idx + 1,
      }));

      const formattedSteps = validSteps.map((step, idx) => ({
        stepNumber: idx + 1,
        content: step.content,
        imageUrl: step.imageUrl || undefined,
      }));

      await recipeApi.create({
        recipe,
        ingredients: formattedIngredients,
        steps: formattedSteps,
      });

      navigate('/');
    } catch (err: any) {
      alert(err.message || '创建失败');
    } finally {
      setLoading(false);
    }
  };

  const addIngredient = () => {
    setIngredients([...ingredients, { name: '', amount: '' }]);
  };

  const removeIngredient = (index: number) => {
    if (ingredients.length > 1) {
      setIngredients(ingredients.filter((_, i) => i !== index));
    }
  };

  const updateIngredient = (index: number, field: 'name' | 'amount', value: string) => {
    const updated = [...ingredients];
    updated[index][field] = value;
    setIngredients(updated);
  };

  const addStep = () => {
    setSteps([...steps, { stepNumber: steps.length + 1, content: '', imageUrl: '' }]);
  };

  const removeStep = (index: number) => {
    if (steps.length > 1) {
      const updated = steps.filter((_, i) => i !== index).map((step, i) => ({
        ...step,
        stepNumber: i + 1,
      }));
      setSteps(updated);
    }
  };

  const updateStep = (index: number, field: 'content' | 'imageUrl', value: string) => {
    const updated = [...steps];
    updated[index][field] = value;
    setSteps(updated);
  };

  return (
    <div className="create-recipe-page">
      <div className="create-recipe-container">
        <div className="create-recipe-header">
          <button className="back-button" onClick={() => navigate(-1)}>
            <ArrowLeft size={20} />
            返回
          </button>
          <h1>
            <ChefHat size={28} />
            创建新食谱
          </h1>
        </div>

        <form onSubmit={handleSubmit} className="recipe-form">
          <div className="form-section">
            <h2>基本信息</h2>
            
            <div className="form-group">
              <label>食谱标题 *</label>
              <input
                type="text"
                value={formData.title}
                onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                placeholder="给您的食谱起个名字"
                required
              />
            </div>

            <div className="form-group">
              <label>简介描述</label>
              <textarea
                value={formData.description}
                onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                placeholder="简单描述一下这道菜"
                rows={3}
              />
            </div>

            <div className="form-row">
              <div className="form-group">
                <label>分类</label>
                <select
                  value={formData.category}
                  onChange={(e) => setFormData({ ...formData, category: e.target.value })}
                >
                  {CATEGORIES.map(cat => (
                    <option key={cat} value={cat}>{cat}</option>
                  ))}
                </select>
              </div>

              <div className="form-group">
                <label>难度</label>
                <select
                  value={formData.difficulty}
                  onChange={(e) => setFormData({ ...formData, difficulty: e.target.value })}
                >
                  {DIFFICULTIES.map(diff => (
                    <option key={diff} value={diff}>{diff}</option>
                  ))}
                </select>
              </div>

              <div className="form-group">
                <label>烹饪时间</label>
                <div className="input-with-unit">
                  <input
                    type="number"
                    value={formData.cookTime}
                    onChange={(e) => setFormData({ ...formData, cookTime: parseInt(e.target.value) || 0 })}
                    min="1"
                    max="999"
                  />
                  <span>分钟</span>
                </div>
              </div>

              <div className="form-group">
                <label>份量</label>
                <div className="input-with-unit">
                  <input
                    type="number"
                    value={formData.servings}
                    onChange={(e) => setFormData({ ...formData, servings: parseInt(e.target.value) || 1 })}
                    min="1"
                    max="100"
                  />
                  <span>人份</span>
                </div>
              </div>
            </div>

            <div className="form-group">
              <label>封面图片</label>
              <div className="cover-image-upload">
                <input
                  type="file"
                  accept="image/*"
                  onChange={handleCoverImageChange}
                  disabled={uploadingImage}
                  className="file-input"
                  id="cover-image-input"
                />
                <label htmlFor="cover-image-input" className="upload-label">
                  {uploadingImage ? (
                    <span className="uploading-text">上传中...</span>
                  ) : formData.imageUrl ? (
                    <img src={formData.imageUrl} alt="封面预览" className="cover-preview" />
                  ) : (
                    <>
                      <Upload size={32} className="upload-icon" />
                      <span className="upload-text">点击上传封面图片</span>
                      <span className="upload-hint">支持 JPG、PNG，最大 5MB</span>
                    </>
                  )}
                </label>
                {formData.imageUrl && (
                  <button
                    type="button"
                    className="remove-cover-button"
                    onClick={() => setFormData({ ...formData, imageUrl: '' })}
                  >
                    <Trash2 size={16} />
                  </button>
                )}
              </div>
            </div>
          </div>

          <div className="form-section">
            <div className="section-header">
              <h2>食材清单</h2>
              <button type="button" className="add-button" onClick={addIngredient}>
                <Plus size={18} />
                添加食材
              </button>
            </div>

            <div className="ingredients-list">
              {ingredients.map((ing, index) => (
                <div key={index} className="ingredient-row">
                  <input
                    type="text"
                    value={ing.name}
                    onChange={(e) => updateIngredient(index, 'name', e.target.value)}
                    placeholder="食材名称"
                  />
                  <input
                    type="text"
                    value={ing.amount}
                    onChange={(e) => updateIngredient(index, 'amount', e.target.value)}
                    placeholder="用量"
                  />
                  <button
                    type="button"
                    className="remove-button"
                    onClick={() => removeIngredient(index)}
                    disabled={ingredients.length === 1}
                  >
                    <Trash2 size={18} />
                  </button>
                </div>
              ))}
            </div>
          </div>

          <div className="form-section">
            <div className="section-header">
              <h2>烹饪步骤</h2>
              <button type="button" className="add-button" onClick={addStep}>
                <Plus size={18} />
                添加步骤
              </button>
            </div>

            <div className="steps-list">
              {steps.map((step, index) => (
                <div key={index} className="step-row">
                  <div className="step-number">{index + 1}</div>
                  <div className="step-content">
                    <textarea
                      value={step.content}
                      onChange={(e) => updateStep(index, 'content', e.target.value)}
                      placeholder={`步骤 ${index + 1} 的详细说明...`}
                      rows={3}
                    />
                    <div className="step-image-upload">
                      <input
                        type="file"
                        accept="image/*"
                        onChange={(e) => handleStepImageChange(index, e)}
                        disabled={uploadingStepImage === index}
                        className="file-input"
                        id={`step-image-${index}`}
                      />
                      <label htmlFor={`step-image-${index}`} className={`step-upload-label ${uploadingStepImage === index ? 'uploading' : ''}`}>
                        {uploadingStepImage === index ? (
                          <span>上传中...</span>
                        ) : step.imageUrl ? (
                          <img src={step.imageUrl} alt={`步骤${index + 1}`} className="step-thumb" />
                        ) : (
                          <>
                            <Image size={16} />
                            <span>添加步骤图片</span>
                          </>
                        )}
                      </label>
                      {step.imageUrl && (
                        <button
                          type="button"
                          className="remove-step-image"
                          onClick={() => updateStep(index, 'imageUrl', '')}
                        >
                          <Trash2 size={14} />
                        </button>
                      )}
                    </div>
                  </div>
                  <button
                    type="button"
                    className="remove-button"
                    onClick={() => removeStep(index)}
                    disabled={steps.length === 1}
                  >
                    <Trash2 size={18} />
                  </button>
                </div>
              ))}
            </div>
          </div>

          <div className="form-actions">
            <button type="button" className="cancel-button" onClick={() => navigate(-1)}>
              取消
            </button>
            <button type="submit" className="submit-button" disabled={loading}>
              <Save size={18} />
              {loading ? '发布中...' : '发布食谱'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
