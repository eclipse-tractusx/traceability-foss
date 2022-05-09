export enum QualityAlertFlow {
  BOTTOM_UP = 'BOTTOM-UP',
  TOP_DOWN = 'TOP-DOWN',
}

export enum QualityTypes {
  MINOR = 'rgba(255, 199, 31, 0.6)',
  MAJOR = 'rgba(254, 103, 2, 0.6)',
  CRITICAL = '#c9585a',
  'LIFE-THREATENING' = '#905680',
}

export enum QualityAlertIcons {
  MINOR = 'error-warning-line',
  MAJOR = 'alert-line',
  CRITICAL = 'spam-line',
  'LIFE-THREATENING' = 'close-circle-line',
}

export enum QualityAlertTypes {
  PENDING = 'pending',
  EXTERNAL = 'external',
  DISTRIBUTED = 'committed',
  CREATED = 'created',
}
