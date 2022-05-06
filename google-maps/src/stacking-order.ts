const props = /\b(?:position|zIndex|opacity|transform|webkitTransform|mixBlendMode|filter|webkitFilter|isolation)\b/;

function isFlexItem(node: Element) {
	const display = getComputedStyle(node.parentNode as Element).display;
	return display === 'flex' || display === 'inline-flex';
}

function createsStackingContext(node: Element) {
	const style = getComputedStyle(node) as any;

	// https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_Positioning/Understanding_z_index/The_stacking_context
	if (style.position === 'fixed') return true;
	if (style.zIndex !== 'auto' && style.position !== 'static' || isFlexItem(node)) return true;
	if (+style.opacity < 1) return true;
	if ('transform' in style && style.transform !== 'none') return true;
	if ('webkitTransform' in style && style.webkitTransform !== 'none') return true;
	if ('mixBlendMode' in style && style.mixBlendMode !== 'normal') return true;
	if ('filter' in style && style.filter !== 'none') return true;
	if ('webkitFilter' in style && style.webkitFilter !== 'none') return true;
	if ('isolation' in style && style.isolation === 'isolate') return true;
	if (props.test(style.willChange)) return true;
	if (style.webkitOverflowScrolling === 'touch') return true;

	return false;
}

function findStackingContext(nodes: string | any[]) {
	let i = nodes.length;

	while (i--) {
		if (createsStackingContext(nodes[i])) return nodes[i];
	}

	return null;
}

function getAncestors(node: { parentNode: any; }) {
	const ancestors = [];

	while (node) {
		ancestors.push(node);
		node = node.parentNode;
	}

	return ancestors; // [ node, ... <body>, <html>, document ]
}

function getZIndex(node: Element) {
	return node && Number(getComputedStyle(node).zIndex) || 0;
}

function last(array: string | any[]) {
	return array[array.length - 1];
}

export function CompareStackingOrder(a: any, b: any): number {
	if (a === b) throw new Error('Cannot compare node with itself');

	const ancestors = {
		a: getAncestors(a),
		b: getAncestors(b)
	};

	let commonAncestor = undefined;

	// remove shared ancestors
	while (last(ancestors.a) === last(ancestors.b)) {
		a = ancestors.a.pop();
		b = ancestors.b.pop();

		commonAncestor = a;
	}

	const stackingContexts = {
		a: findStackingContext(ancestors.a),
		b: findStackingContext(ancestors.b)
	};

	const zIndexes = {
		a: getZIndex(stackingContexts.a),
		b: getZIndex(stackingContexts.b)
	};

	if (zIndexes.a === zIndexes.b) {
		const children = commonAncestor.childNodes;

		const furthestAncestors = {
			a: last(ancestors.a),
			b: last(ancestors.b)
		};

		let i = children.length;
		while (i--) {
			const child = children[i];
			if (child === furthestAncestors.a) return 1;
			if (child === furthestAncestors.b) return -1;
		}
	}

	return Math.sign(zIndexes.a - zIndexes.b);
}