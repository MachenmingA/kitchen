import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ChefHat, Plus, Trash2, Save, ArrowLeft, Image } from 'lucide-react';
import { recipeApi, getCurrentUser } from '../../services/api';
import './CreateRecipePage.css';

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
              <label>封面图片 URL</label>
              <div className="image-input-wrapper">
                <Image size={20} className="image-icon" />
                <input
                  type="text"
                  value={formData.imageUrl}
                  onChange={(e) => setFormData({ ...formData, imageUrl: e.target.value })}
                  placeholder="输入图片地址，或留空使用默认图片"
                />
              </div>
              {formData.imageUrl && (
                <img src={formData.imageUrl} alt="预览" className="image-preview" onError={(e) => (e.target as HTMLImageElement).style.display = 'none'} />
              )}
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
                    <input
                      type="text"
                      value={step.imageUrl}
                      onChange={(e) => updateStep(index, 'imageUrl', e.target.value)}
                      placeholder="步骤图片 URL（选填）"
                      className="step-image-url"
                    />
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
