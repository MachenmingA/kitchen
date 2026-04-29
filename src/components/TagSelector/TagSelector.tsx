import { useState, useEffect } from 'react';
import { Hash, X, Plus } from 'lucide-react';
import { tagApi } from '../../services/api';
import type { Tag as TagType } from '../../services/api';
import './TagSelector.css';

interface TagSelectorProps {
  selectedTags: number[];
  onChange: (tagIds: number[]) => void;
}

export function TagSelector({ selectedTags, onChange }: TagSelectorProps) {
  const [tags, setTags] = useState<TagType[]>([]);
  const [loading, setLoading] = useState(true);
  const [newTagName, setNewTagName] = useState('');
  const [showInput, setShowInput] = useState(false);

  useEffect(() => {
    loadTags();
  }, []);

  const loadTags = async () => {
    setLoading(true);
    try {
      const popularTags = await tagApi.getPopular(20);
      setTags(popularTags);
    } catch (error) {
      console.error('Failed to load tags:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleToggleTag = (tagId: number) => {
    if (selectedTags.includes(tagId)) {
      onChange(selectedTags.filter(id => id !== tagId));
    } else {
      onChange([...selectedTags, tagId]);
    }
  };

  const handleCreateTag = async () => {
    if (!newTagName.trim()) return;

    try {
      const newTag = await tagApi.create(newTagName.trim());
      setTags([newTag, ...tags]);
      onChange([...selectedTags, newTag.id]);
      setNewTagName('');
      setShowInput(false);
    } catch (error) {
      console.error('Failed to create tag:', error);
    }
  };

  const selectedTagObjects = tags.filter(tag => selectedTags.includes(tag.id));

  return (
    <div className="tag-selector">
      <div className="tag-selector-label">
        <Hash size={16} />
        <span>标签</span>
      </div>

      {selectedTagObjects.length > 0 && (
        <div className="selected-tags">
          {selectedTagObjects.map(tag => (
            <span key={tag.id} className="selected-tag">
              {tag.name}
              <button onClick={() => handleToggleTag(tag.id)}>
                <X size={14} />
              </button>
            </span>
          ))}
        </div>
      )}

      {loading ? (
        <div className="tag-loading">加载中...</div>
      ) : (
        <div className="tag-list">
          {tags
            .filter(tag => !selectedTags.includes(tag.id))
            .map(tag => (
              <button
                key={tag.id}
                className="tag-item"
                onClick={() => handleToggleTag(tag.id)}
              >
                {tag.name}
                <span className="tag-count">{tag.recipeCount}</span>
              </button>
            ))}
          
          {showInput ? (
            <div className="new-tag-input">
              <input
                type="text"
                value={newTagName}
                onChange={(e) => setNewTagName(e.target.value)}
                placeholder="输入标签名"
                autoFocus
                onKeyDown={(e) => e.key === 'Enter' && handleCreateTag()}
              />
              <button onClick={handleCreateTag}>添加</button>
              <button onClick={() => setShowInput(false)} className="cancel">
                <X size={16} />
              </button>
            </div>
          ) : (
            <button className="add-tag-btn" onClick={() => setShowInput(true)}>
              <Plus size={16} />
              新建标签
            </button>
          )}
        </div>
      )}
    </div>
  );
}
